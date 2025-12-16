package com.toki.model; 

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.LinkedHashMap;

public class WeeklySchedule {
    
    private final Map<DayOfWeek, List<AgendaAbstract>> scheduleMap;

    public WeeklySchedule(Map<DayOfWeek, List<AgendaAbstract>> scheduleMap) {
        this.scheduleMap = new LinkedHashMap<>();
        
        for (DayOfWeek day : DayOfWeek.values()) {
            this.scheduleMap.put(day, scheduleMap.getOrDefault(day, Collections.emptyList()));
        }
    }

    public List<AgendaAbstract> getItemsForDay(DayOfWeek day) {
        return scheduleMap.getOrDefault(day, Collections.emptyList());
    }
    
    public Map<DayOfWeek, List<AgendaAbstract>> getScheduleMap() {
        return scheduleMap;
    }
}