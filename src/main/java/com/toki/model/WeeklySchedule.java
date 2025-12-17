package com.toki.model;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

public class WeeklySchedule {

    private final Map<DayOfWeek, List<AgendaAbstract>> scheduleMap;

    /**
     * Constructs a WeeklySchedule with the given map of agendas.
     * 
     * @param scheduleMap Map of DayOfWeek to list of agendas.
     */
    public WeeklySchedule(Map<DayOfWeek, List<AgendaAbstract>> scheduleMap) {
        this.scheduleMap = scheduleMap;
    }

    /**
     * Retrieves the list of agendas for a specific day.
     * 
     * @param day The day of the week.
     * @return List of agendas for that day.
     */
    public List<AgendaAbstract> getItemsForDay(DayOfWeek day) {
        return scheduleMap.get(day);
    }

    /**
     * Retrieves the entire schedule map.
     * 
     * @return Map of DayOfWeek to list of agendas.
     */
    public Map<DayOfWeek, List<AgendaAbstract>> getScheduleMap() {
        return scheduleMap;
    }
}