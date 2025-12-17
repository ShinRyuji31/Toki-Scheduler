package com.toki.service;

import com.toki.model.*;
import com.toki.repository.*;
import org.junit.Before;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SchedulerServiceTest {

    private SchedulerService schedulerService;
    private Agenda_RepositoryInterface<AgendaRegular> regularRepo;
    private Agenda_RepositoryInterface<AgendaSpecial> specialRepo;
    private Agenda_RepositoryInterface<AgendaTask> taskRepo;

    @Before
    public void setUp() {
        // Mock Repositories with in-memory anonymous classes or manual stubs
        // Since we don't have Mockito, we will use simple stubs.

        regularRepo = new AgendaRegularRepository() {
            @Override
            public List<AgendaRegular> findAll() {
                return Arrays.asList(
                        new AgendaRegular(1, "Gym", "Personal", "Health", DayOfWeek.MONDAY, LocalTime.of(8, 0),
                                "Train legs"));
            }
        };

        specialRepo = new AgendaSpecialRepository() {
            @Override
            public List<AgendaSpecial> findAll() {
                return Arrays.asList(
                        new AgendaSpecial(2, "Concert", "Entertainment", "Fun", LocalDate.now(), LocalTime.of(19, 0),
                                "VIP Seats"));
            }
        };

        taskRepo = new AgendaTaskRepository() {
            @Override
            public List<AgendaTask> findAll() {
                return Arrays.asList(
                        new AgendaTask(3, "Homework", "School", "Education", LocalDate.now(), 'H', "Math"));
            }
        };

        schedulerService = new SchedulerService(regularRepo, specialRepo, taskRepo);
    }

    @Test
    public void testGenerateWeeklySchedule() {
        LocalDate today = LocalDate.now();
        WeeklySchedule schedule = schedulerService.generateWeeklySchedule(today);
        Map<DayOfWeek, List<AgendaAbstract>> map = schedule.getScheduleMap();

        // Check Monday (AgendaRegular)
        List<AgendaAbstract> mondayItems = map.get(DayOfWeek.MONDAY);
        assertFalse("Monday should not be empty (Gym)", mondayItems.isEmpty());
        // Simple check if it contains our Gym item by specific logic (or just size)
        boolean foundGym = mondayItems.stream().anyMatch(a -> a.getTitle().equals("Gym"));
        // Note: Only if today is within the week that regular agenda is considered
        // effective?
        // Logic says regular agendas repeat weekly, so it should be there.
        assertTrue(foundGym);
    }

    @Test
    public void testGetUpcomingTasks() {
        LocalDate today = LocalDate.now();
        List<AgendaTask> tasks = schedulerService.getUpcomingTasks(today, 7);
        assertEquals(1, tasks.size());
        assertEquals("Homework", tasks.get(0).getTitle());
    }

    @Test
    public void testGetAgendasForDay() {
        LocalDate today = LocalDate.now(); // Both special and task are set for today
        List<AgendaAbstract> agendas = schedulerService.getAgendasForDay(today);

        // Should find Special and Task.
        // If today is Monday, it might also find Gym.
        int expectedSize = 2;
        if (today.getDayOfWeek() == DayOfWeek.MONDAY) {
            expectedSize = 3;
        }

        assertTrue("Should have at least 2 items today", agendas.size() >= 2);
    }

    @Test
    public void testGetAgendaCounts() {
        Map<String, Long> counts = schedulerService.getAgendaCounts();
        assertEquals(Long.valueOf(1), counts.get("Regular"));
        assertEquals(Long.valueOf(1), counts.get("Special"));
        assertEquals(Long.valueOf(1), counts.get("Task"));
    }
}
