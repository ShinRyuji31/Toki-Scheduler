package com.toki.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class AgendaSpecial extends AgendaAbstract { 
    private LocalDate date; 
    private LocalTime time;

    public AgendaSpecial(int ID, String title, String type, String group, LocalDate date, LocalTime time, String notes ) {
        super(ID, title, type, group, notes); 
        this.date = date;
        this.time = time;
    }

    public LocalDate getRelevantDate() { return this.date; }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

}