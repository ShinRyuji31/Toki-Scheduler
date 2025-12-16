package com.toki.model;

import java.time.LocalDate;

public abstract class AgendaAbstract {
    private int ID;
    private String title;
    private String type;
    private String group;
    private String notes;

    public AgendaAbstract(int ID, String title, String type, String group, String notes) {
        this.ID = ID;
        this.title = title;
        this.type = type;
        this.group = group;
        this.notes = notes;
    }

    public abstract LocalDate getRelevantDate();

    public int getID() {
        return ID;
    }
    
    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}