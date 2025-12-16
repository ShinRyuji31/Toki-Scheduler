package com.toki.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public class AgendaRegular extends AgendaAbstract { 
    private DayOfWeek day;  
    private LocalTime time;  

    public AgendaRegular(int ID, String title, String type, String group, DayOfWeek day, LocalTime time, String notes) {
        super(ID, title, type, group, notes);
        this.day = day;
        this.time = time;
    }
    
    public LocalDate getRelevantDate() { return null; }

    public DayOfWeek getDay() {
        return day;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    } 
 
    public LocalTime getTime() {
        return time;
    }  
     
    public void setTime(LocalTime time) {
        this.time = time;
    }
}