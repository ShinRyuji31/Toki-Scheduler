package com.toki.ui.controller;

import com.toki.model.*;
import com.toki.service.SchedulerService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Controller for handling logic related to Agenda creation and updates.
 */
public class AgendaController {

    private final SchedulerService schedulerService;

    public AgendaController(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    /**
     * Handles the logic for saving different types of Agendas.
     * 
     * @param agendaType   The type of Agenda ("Task", "Special", or "Regular").
     * @param title        The title of the Agenda (required).
     * @param group        The group of the Agenda.
     * @param notes        Notes for the Agenda.
     * @param taskDue      Due date for the Task (only for Task type).
     * @param taskPriority Priority of the Task (H/M/L) (only for Task type).
     * @param specialDate  Date of the Special Agenda (only for Special type).
     * @param specialTime  Time of the Special Agenda (only for Special type).
     * @param regularDay   Day of the Regular Agenda (only for Regular type).
     * @param regularTime  Time of the Regular Agenda (only for Regular type).
     * @return Success or error message.
     */
    public String handleSave(
            String agendaType,
            String title,
            String group,
            String notes,
            LocalDate taskDue,
            char taskPriority,
            LocalDate specialDate,
            LocalTime specialTime,
            DayOfWeek regularDay,
            LocalTime regularTime) {

        if (title.trim().isEmpty()) {
            return "error:Title Cannot Be Empty";
        }

        // Get next ID from service
        int nextId = schedulerService.getNextAgendaId();
        String groupName = group.trim().isEmpty() ? null : group.trim();

        try {
            switch (agendaType) {
                case "Task":
                    if (taskDue == null)
                        return "error:Due Date is required for Task";
                    AgendaTask newTask = new AgendaTask(nextId, title, agendaType, groupName, taskDue, taskPriority,
                            notes);
                    schedulerService.getTaskRepo().save(newTask);
                    break;
                case "Special":
                    if (specialDate == null || specialTime == null)
                        return "error:Date and Time are required for Special Agenda";
                    AgendaSpecial newSpecial = new AgendaSpecial(nextId, title, agendaType, groupName, specialDate,
                            specialTime, notes);
                    schedulerService.getSpecialRepo().save(newSpecial);
                    break;
                case "Regular":
                    if (regularDay == null || regularTime == null)
                        return "error:Day and Time are required for Regular Agenda";
                    AgendaRegular newRegular = new AgendaRegular(nextId, title, agendaType, groupName, regularDay,
                            regularTime, notes);
                    schedulerService.getRegularRepo().save(newRegular);
                    break;
                default:
                    return "error:Invalid Agenda Type";
            }
            return "success:Agenda --" + title + "-- Saved Successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "error:Failed to save agenda. Detail: " + e.getMessage();
        }
    }
}