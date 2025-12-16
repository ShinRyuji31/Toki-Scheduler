package com.toki.ui.view;

import com.toki.model.AgendaAbstract;
import com.toki.model.User;
import com.toki.model.WeeklySchedule;
import com.toki.service.SchedulerService;
import com.toki.ui.MainApp;
import com.toki.ui.components.DashboardDayPanel;
import com.toki.ui.components.DigitalClock;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.util.stream.Collectors;

/**
 * Dashboard View for the Scheduler application.
 * displays the weekly schedule, quick actions, and statistics.
 */
public class DashboardView {

    private final Stage stage;
    private final SchedulerService schedulerService;
    private final User currentUser;

    private Label welcomeLabel;
    private HBox weeklyScheduleContainer;
    private VBox quickActionsPanel;
    private VBox statsPanel;

    /**
     * Constructs the DashboardView.
     * 
     * @param stage            The primary stage of the application.
     * @param schedulerService The service for scheduling operations.
     * @param currentUser      The currently logged-in user.
     */
    public DashboardView(Stage stage, SchedulerService schedulerService, User currentUser) {
        this.stage = stage;
        this.schedulerService = schedulerService;
        this.currentUser = currentUser;

        initializeUI();
        loadData();
    }

    /**
     * Refreshes the dashboard data.
     */
    public void refresh() {
        loadData();
    }

    private void initializeUI() {
        String username = currentUser != null ? currentUser.getUsername() : "Guest";
        welcomeLabel = new Label("Hello, " + username + "!");
        welcomeLabel.getStyleClass().add("header-title");

        weeklyScheduleContainer = new HBox(10);
        weeklyScheduleContainer.getStyleClass().add("weekly-schedule-container");

        quickActionsPanel = createQuickActionsPanel();
        statsPanel = createStatsPanel();
    }

    private VBox createQuickActionsPanel() {
        VBox panel = new VBox(10);
        panel.getStyleClass().add("card");
        panel.getStyleClass().add("sidebar-card");
        panel.setPadding(new Insets(15));

        Label titleLabel = new Label("Quick Action ⚡");
        titleLabel.getStyleClass().add("card-title");
        panel.getChildren().add(titleLabel);

        Button addTaskButton = new Button("➕ New Task");
        addTaskButton.getStyleClass().addAll("action-button", "primary");
        addTaskButton.setOnAction(e -> handleAddTask());

        Button addSpecialButton = new Button("➕ New Agenda");
        addSpecialButton.getStyleClass().add("action-button");
        addSpecialButton.setOnAction(e -> handleAddSpecial());

        Button viewAllButton = new Button("📂 All Agenda");
        viewAllButton.getStyleClass().add("action-button");
        viewAllButton.setOnAction(e -> handleViewAll());

        panel.getChildren().addAll(addTaskButton, addSpecialButton, viewAllButton);
        return panel;
    }

    private VBox createStatsPanel() {
        VBox panel = new VBox(10);
        panel.getStyleClass().add("card");
        panel.getStyleClass().add("sidebar-card");
        panel.setPadding(new Insets(15));

        Label titleLabel = new Label("Summary 📊");
        titleLabel.getStyleClass().add("card-title");

        panel.getChildren().add(titleLabel);
        statsPanel = panel;
        return panel;
    }

    private void handleAddTask() {
        MainApp.showAgendaForm("Task");
    }

    private void handleAddSpecial() {
        MainApp.showAgendaForm("Special");
    }

    private void handleViewAll() {
        MainApp.showAgendaList();
    }

    private void loadData() {
        LocalDate today = LocalDate.now();
        WeeklySchedule weeklySchedule = schedulerService.generateWeeklySchedule(today);
        Map<String, Long> counts = schedulerService.getAgendaCounts();

        updateWeeklySchedule(weeklySchedule, today);
        updateStatsPanel(counts);
    }

    private void updateWeeklySchedule(WeeklySchedule weeklySchedule, LocalDate today) {
        weeklyScheduleContainer.getChildren().clear();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        List<DayOfWeek> orderedDays = weeklySchedule.getScheduleMap().keySet().stream()
                .sorted()
                .collect(Collectors.toList());

        for (DayOfWeek day : orderedDays) {
            List<AgendaAbstract> agendas = weeklySchedule.getScheduleMap().get(day);
            LocalDate dateForPanel = startOfWeek.with(TemporalAdjusters.nextOrSame(day));
            DashboardDayPanel dayPanel = new DashboardDayPanel(dateForPanel, agendas);

            HBox.setHgrow(dayPanel, Priority.ALWAYS);
            dayPanel.setMinWidth(100);
            if (dateForPanel.isEqual(today)) {
                dayPanel.getStyleClass().add("today");
            }
            weeklyScheduleContainer.getChildren().add(dayPanel);
        }
    }

    private void updateStatsPanel(Map<String, Long> counts) {
        statsPanel.getChildren().removeIf(node -> !node.getStyleClass().contains("card-title"));

        addStatLine(statsPanel, "Task", counts.getOrDefault("Task", 0L));
        addStatLine(statsPanel, "Special", counts.getOrDefault("Special", 0L));
        addStatLine(statsPanel, "Regular", counts.getOrDefault("Regular", 0L));
    }

    private void addStatLine(VBox parent, String labelText, long value) {
        HBox line = new HBox();
        line.getStyleClass().add("stat-line");

        Label label = new Label(labelText + ":");
        HBox.setHgrow(label, Priority.ALWAYS);

        Label valueLabel = new Label(String.valueOf(value));
        valueLabel.getStyleClass().add("stat-value");

        line.getChildren().addAll(label, valueLabel);
        parent.getChildren().add(line);
    }

    /**
     * Converts priority char to a display string.
     * 
     * @param priority The priority char (H, M, L).
     * @return The string representation.
     */
    public static String getPriorityStringStatic(char priority) {
        return switch (Character.toUpperCase(priority)) {
            case 'H' -> "HIGH";
            case 'M' -> "MEDIUM";
            case 'L' -> "LOW";
            default -> "N/A";
        };
    }

    /**
     * Gets the view node of this Dashboard.
     * 
     * @return The parent node containing the dashboard UI.
     */
    public Parent getView() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("dashboard-root");

        // TOP -- Header Bar ---
        DigitalClock digitalClock = new DigitalClock();

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(10, welcomeLabel, spacer, digitalClock);
        header.setPadding(new Insets(20, 20, 10, 20));
        header.getStyleClass().add("header-bar");
        root.setTop(header);

        // LEFT -- Sidebar ---
        VBox sidebar = new VBox(20);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(20, 10, 20, 20));
        sidebar.setMinWidth(250);
        sidebar.setMaxWidth(250);
        sidebar.getChildren().addAll(quickActionsPanel, statsPanel);
        root.setLeft(sidebar);

        // CENTER -- Main Content Area ---
        VBox mainContent = new VBox(20);
        mainContent.getStyleClass().add("main-content-area");
        mainContent.setPadding(new Insets(20));

        LocalDate today = LocalDate.now();
        String month = today.getMonth().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH);

        Label weekTitle = new Label(month);
        weekTitle.getStyleClass().add("section-title");

        mainContent.getChildren().addAll(weekTitle, weeklyScheduleContainer);

        VBox.setVgrow(weeklyScheduleContainer, Priority.ALWAYS);

        root.setCenter(mainContent);

        return root;
    }
}