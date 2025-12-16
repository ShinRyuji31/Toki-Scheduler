package com.toki.ui.components;

import com.toki.model.AgendaAbstract;
import com.toki.model.AgendaRegular;
import com.toki.model.AgendaSpecial;
import com.toki.model.AgendaTask;
import com.toki.ui.view.AgendaDetailsView;
import com.toki.ui.view.DashboardView;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Panel representing a single day in the dashboard.
 * Displays a list of agendas for that specific day.
 */
public class DashboardDayPanel extends VBox {

    private final LocalDate date;
    private final DayOfWeek dayOfWeek;
    private final List<AgendaAbstract> agendas;

    /**
     * Constructs a DashboardDayPanel.
     * 
     * @param date    The date this panel represents.
     * @param agendas The list of agendas for this day.
     */
    public DashboardDayPanel(LocalDate date, List<AgendaAbstract> agendas) {
        super(5);
        this.date = date;
        this.dayOfWeek = date.getDayOfWeek();

        this.agendas = agendas.stream()
                .sorted(Comparator
                        .comparing(this::getTimeBasedOrder)
                        .thenComparing(AgendaAbstract::getTitle))
                .collect(Collectors.toList());

        initializePanel();
    }

    private void initializePanel() {
        this.getStyleClass().addAll("card", "day-panel");
        this.setPadding(new Insets(10));

        VBox header = new VBox(0);
        header.setAlignment(Pos.CENTER);
        header.getStyleClass().add("day-panel-header");

        Label dateLabel = new Label(String.valueOf(date.getDayOfMonth()));
        dateLabel.getStyleClass().add("day-panel-date");

        // Ensure day name is in English
        String dayName = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        Label dayLabel = new Label(dayName);
        dayLabel.getStyleClass().add("day-panel-day");

        header.getChildren().addAll(dateLabel, dayLabel);

        VBox separator = new VBox();
        separator.getStyleClass().add("day-panel-separator");

        this.getChildren().addAll(header, separator);

        loadAgendas();

        VBox contentWrapper = new VBox(5);
        // Skip header and separator to get content
        contentWrapper.getChildren().addAll(this.getChildren().stream().skip(2).collect(Collectors.toList()));
        VBox.setVgrow(contentWrapper, Priority.ALWAYS);

        this.getChildren().clear();
        this.getChildren().addAll(header, separator, contentWrapper);
    }

    private void loadAgendas() {
        if (agendas.isEmpty()) {
            return;
        }

        agendas.forEach(this::addAgendaItem);
    }

    private String getTimeBasedOrder(AgendaAbstract agenda) {
        if (agenda instanceof AgendaSpecial special) {
            return special.getTime().toString() + "-Special";
        } else if (agenda instanceof AgendaRegular regular) {
            return regular.getTime().toString() + "-Regular";
        } else if (agenda instanceof AgendaTask task) {
            char priority = task.getPriority();
            return switch (Character.toUpperCase(priority)) {
                case 'H' -> "Z-A";
                case 'M' -> "Z-B";
                case 'L' -> "Z-C";
                default -> "Z-D";
            };
        }
        return "Y-Default";
    }

    private void addAgendaItem(AgendaAbstract agenda) {
        HBox itemBox = new HBox(5);
        itemBox.getStyleClass().add("day-agenda-item");

        // Add click handler to show details
        itemBox.setOnMouseClicked(e -> AgendaDetailsView.show(agenda));
        itemBox.setStyle("-fx-cursor: hand;");

        String timeInfo;
        String typeStyle;

        if (agenda instanceof AgendaSpecial special) {
            timeInfo = special.getTime().toString();
            typeStyle = "type-special";
        } else if (agenda instanceof AgendaRegular regular) {
            timeInfo = regular.getTime().toString();
            typeStyle = "type-regular";
        } else if (agenda instanceof AgendaTask task) {
            timeInfo = "Task";
            typeStyle = "type-task";

            String priorityFull = DashboardView.getPriorityStringStatic(task.getPriority());
            Label priorityLabel = new Label(priorityFull.substring(0, 1));
            priorityLabel.getStyleClass().addAll("task-priority-indicator", "priority-" + task.getPriority());
            itemBox.getChildren().add(priorityLabel);
        } else {
            timeInfo = "N/A";
            typeStyle = "type-default";
        }

        Label timeLabel = new Label(timeInfo);
        timeLabel.getStyleClass().add("agenda-time");

        Label titleLabel = new Label(agenda.getTitle());
        titleLabel.getStyleClass().add("agenda-title");
        HBox.setHgrow(titleLabel, Priority.ALWAYS);

        itemBox.getChildren().addAll(timeLabel, titleLabel);
        this.getChildren().add(itemBox);
    }
}
