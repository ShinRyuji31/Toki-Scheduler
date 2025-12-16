package com.toki.model;

import java.time.LocalDate;

public class AgendaTask extends AgendaAbstract {
    private LocalDate due;
    private char priority;

    public AgendaTask(int ID, String title, String type, String group, LocalDate due, char priority, String notes) {
        super(ID, title, type, group, notes);
        this.due = due;
        this.priority = priority;
    }

    public LocalDate getRelevantDate() { return this.due; }

    public LocalDate getDue() {
        return due;
    }

    public void setDue(LocalDate due) {
        this.due = due;
    }
    
    public char getPriority() {
        return priority;
    }

    public void setPriority(char priority) {
        this.priority = priority;
    }
}