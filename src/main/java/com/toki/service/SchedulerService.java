package com.toki.service;

import com.toki.model.*;
import com.toki.repository.Agenda_RepositoryInterface;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class responsible for business logic related to scheduling.
 * Handles agenda retrieval, scheduling generation, and statistics.
 */
public class SchedulerService {

    private final Agenda_RepositoryInterface<AgendaRegular> regularRepo;
    private final Agenda_RepositoryInterface<AgendaSpecial> specialRepo;
    private final Agenda_RepositoryInterface<AgendaTask> taskRepo;

    /**
     * Constructs the SchedulerService with necessary repositories.
     * 
     * @param regularRepo Repository for Regular Agendas.
     * @param specialRepo Repository for Special Agendas.
     * @param taskRepo    Repository for Task Agendas.
     */
    public SchedulerService(
            Agenda_RepositoryInterface<AgendaRegular> regularRepo,
            Agenda_RepositoryInterface<AgendaSpecial> specialRepo,
            Agenda_RepositoryInterface<AgendaTask> taskRepo) {
        this.regularRepo = regularRepo;
        this.specialRepo = specialRepo;
        this.taskRepo = taskRepo;
    }

    public Agenda_RepositoryInterface<AgendaRegular> getRegularRepo() {
        return regularRepo;
    }

    public Agenda_RepositoryInterface<AgendaSpecial> getSpecialRepo() {
        return specialRepo;
    }

    public Agenda_RepositoryInterface<AgendaTask> getTaskRepo() {
        return taskRepo;
    }

    /**
     * Generates the weekly schedule for the week containing the given date.
     * 
     * @param today The reference date.
     * @return A WeeklySchedule object containing the map of agendas for the week.
     */
    public WeeklySchedule generateWeeklySchedule(LocalDate today) {
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        Map<DayOfWeek, List<AgendaAbstract>> scheduleMap = initializeWeeklyMap();

        addRegularAgendasToMap(scheduleMap, regularRepo.findAll());
        addOneTimeItemsToMap(scheduleMap, specialRepo.findAll(), startOfWeek, endOfWeek);
        addOneTimeItemsToMap(scheduleMap, taskRepo.findAll(), startOfWeek, endOfWeek);

        for (DayOfWeek day : DayOfWeek.values()) {
            List<AgendaAbstract> items = scheduleMap.get(day);
            items.sort(getAgendaComparator());
        }

        return new WeeklySchedule(scheduleMap);
    }

    /**
     * Retrieves a list of upcoming tasks within a specified number of days.
     * 
     * @param today     The starting date.
     * @param daysAhead The number of days to look ahead.
     * @return List of upcoming AgendaTasks.
     */
    public List<AgendaTask> getUpcomingTasks(LocalDate today, int daysAhead) {
        LocalDate deadline = today.plusDays(daysAhead);

        List<AgendaTask> allTasks = taskRepo.findAll();

        List<AgendaTask> upcomingTasks = allTasks.stream()
                .filter(task -> {
                    LocalDate dueDate = task.getDue();
                    if (dueDate == null)
                        return false;
                    return !dueDate.isBefore(today) && !dueDate.isAfter(deadline);
                })
                .sorted(Comparator
                        .comparing(AgendaTask::getDue)
                        .thenComparingInt(task -> getPriorityValue(task.getPriority())))
                .collect(Collectors.toList());

        return upcomingTasks;
    }

    /**
     * Retrieves all agendas for a specific date.
     * 
     * @param date The date to retrieve agendas for.
     * @return List of agendas for that day.
     */
    public List<AgendaAbstract> getAgendasForDay(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        List<AgendaAbstract> todayAgendas = new ArrayList<>();

        regularRepo.findAll().stream()
                .filter(regular -> regular.getDay() == dayOfWeek)
                .forEach(todayAgendas::add);

        specialRepo.findAll().stream()
                .filter(special -> date.equals(special.getDate()))
                .forEach(todayAgendas::add);

        taskRepo.findAll().stream()
                .filter(task -> date.equals(task.getDue()))
                .forEach(todayAgendas::add);

        todayAgendas.sort(getAgendaComparator());

        return todayAgendas;
    }

    /**
     * Gets count of each agenda type.
     * 
     * @return A map with agenda type as key and count as value.
     */
    public Map<String, Long> getAgendaCounts() {
        Map<String, Long> counts = new HashMap<>();
        long taskCount = taskRepo.findAll().size();
        counts.put("Task", taskCount);
        long specialCount = specialRepo.findAll().size();
        counts.put("Special", specialCount);
        long regularCount = regularRepo.findAll().size();
        counts.put("Regular", regularCount);

        return counts;
    }

    private int getPriorityValue(char priority) {
        switch (Character.toUpperCase(priority)) {
            case 'H':
                return 1;
            case 'M':
                return 2;
            case 'L':
                return 3;
            default:
                return 99;
        }
    }

    private Comparator<AgendaAbstract> getAgendaComparator() {
        return (item1, item2) -> {
            boolean isTask1 = item1 instanceof AgendaTask;
            boolean isTask2 = item2 instanceof AgendaTask;

            if (isTask1 && !isTask2)
                return 1;
            if (!isTask1 && isTask2)
                return -1;

            if (isTask1 && isTask2) {
                AgendaTask task1 = (AgendaTask) item1;
                AgendaTask task2 = (AgendaTask) item2;
                int value1 = getPriorityValue(task1.getPriority());
                int value2 = getPriorityValue(task2.getPriority());
                return Integer.compare(value1, value2);
            } else {
                LocalTime time1 = getAgendaTime(item1);
                LocalTime time2 = getAgendaTime(item2);
                return time1.compareTo(time2);
            }
        };
    }

    private LocalTime getAgendaTime(AgendaAbstract item) {
        if (item instanceof AgendaRegular) {
            return ((AgendaRegular) item).getTime();
        } else if (item instanceof AgendaSpecial) {
            return ((AgendaSpecial) item).getTime();
        }
        return LocalTime.MAX;
    }

    private Map<DayOfWeek, List<AgendaAbstract>> initializeWeeklyMap() {
        Map<DayOfWeek, List<AgendaAbstract>> map = new LinkedHashMap<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            map.put(day, new ArrayList<>());
        }
        return map;
    }

    private void addRegularAgendasToMap(
            Map<DayOfWeek, List<AgendaAbstract>> scheduleMap,
            List<AgendaRegular> regulars) {
        for (AgendaRegular item : regulars) {
            DayOfWeek day = item.getDay();
            if (day != null) {
                scheduleMap.get(day).add(item);
            }
        }
    }

    private <T extends AgendaAbstract> void addOneTimeItemsToMap(
            Map<DayOfWeek, List<AgendaAbstract>> scheduleMap,
            List<T> items,
            LocalDate startOfWeek,
            LocalDate endOfWeek) {
        List<T> filteredItems = items.stream()
                .filter(item -> {
                    LocalDate itemDate = item.getRelevantDate();
                    if (itemDate == null)
                        return false;
                    return !itemDate.isBefore(startOfWeek) && !itemDate.isAfter(endOfWeek);
                })
                .collect(Collectors.toList());

        for (T item : filteredItems) {
            DayOfWeek day = item.getRelevantDate().getDayOfWeek();
            scheduleMap.get(day).add(item);
        }
    }

    /**
     * Determines the next available Agenda ID.
     * 
     * @return The next ID.
     */
    public int getNextAgendaId() {
        int maxId = 0;
        maxId = Math.max(maxId, regularRepo.findAll().stream()
                .mapToInt(AgendaAbstract::getID)
                .max().orElse(0));

        maxId = Math.max(maxId, specialRepo.findAll().stream()
                .mapToInt(AgendaAbstract::getID)
                .max().orElse(0));

        maxId = Math.max(maxId, taskRepo.findAll().stream()
                .mapToInt(AgendaAbstract::getID)
                .max().orElse(0));

        return maxId + 1;
    }
}