package com.toki.ui.forms;

import com.toki.ui.controller.AgendaController;
import com.toki.ui.MainApp;
import com.toki.service.SchedulerService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * Form view for creating or editing an Agenda.
 * Supports different agenda types: Task, Regular, and Special.
 */
public class AgendaForm {

    private final Stage stage;
    private final AgendaController controller;
    private final String initialAgendaType;

    private String currentAgendaType;

    private VBox rootLayout;
    private GridPane formLayout;
    private TextField titleField;
    private TextField groupField;
    private TextArea notesArea;
    private Label messageLabel;

    // Agenda specific fields
    private DatePicker taskDuePicker;
    private ComboBox<Character> taskPriorityCombo;

    private DatePicker specialDate;
    private Spinner<LocalTime> specialTime;
    private ComboBox<DayOfWeek> regularDayCombo;
    private Spinner<LocalTime> regularTime;

    private VBox specialFieldsContainer;
    private VBox regularFieldsContainer;

    private ToggleButton specialButton;
    private ToggleButton regularButton;

    /**
     * Constructs the AgendaForm.
     * 
     * @param stage      The primary stage.
     * @param agendaType The initial type of agenda to create.
     */
    public AgendaForm(Stage stage, String agendaType) {
        this.stage = stage;
        this.initialAgendaType = agendaType;
        this.currentAgendaType = agendaType;

        // Initialize Controller
        SchedulerService schedulerService = MainApp.getSchedulerService();
        this.controller = new AgendaController(schedulerService);

        initializeUI();
    }

    private void initializeUI() {
        // root
        rootLayout = new VBox(20);
        rootLayout.setPadding(new Insets(20));
        rootLayout.getStyleClass().add("form-view-root");

        // title
        Label title = new Label("New Agenda");
        title.getStyleClass().add("form-title");

        // errorLabel
        messageLabel = new Label("");
        messageLabel.getStyleClass().add("message-label");

        // layout
        formLayout = new GridPane();
        formLayout.setHgap(15);
        formLayout.setVgap(15);
        formLayout.setPadding(new Insets(20));
        formLayout.getStyleClass().add("form-pane");

        titleField = new TextField();
        groupField = new TextField();
        notesArea = new TextArea();
        notesArea.setPrefRowCount(3);

        int row = 0;
        addFormField(formLayout, new Label("Title"), titleField, row++);
        addFormField(formLayout, new Label("Group:"), groupField, row++);
        addFormField(formLayout, new Label("Notes:"), notesArea, row++);

        switch (initialAgendaType) {
            case "Task":
                row = addTaskFields(formLayout, row);
                break;
            case "Special":
            case "Regular":
                HBox typeSwitchBox = createTypeSwitchButtons();
                Label typeLabel = new Label("Type:");
                typeLabel.getStyleClass().add("form-label");

                formLayout.add(typeLabel, 0, row);
                formLayout.add(typeSwitchBox, 1, row++);
                GridPane.setHgrow(typeSwitchBox, Priority.ALWAYS);

                specialFieldsContainer = createSpecialFieldsContainer();
                regularFieldsContainer = createRegularFieldsContainer();

                formLayout.add(specialFieldsContainer, 0, row, 2, 1);
                formLayout.add(regularFieldsContainer, 0, row, 2, 1);
                row++;

                boolean isInitialSpecial = "Special".equals(initialAgendaType);

                specialFieldsContainer.setVisible(isInitialSpecial);
                specialFieldsContainer.setManaged(isInitialSpecial);
                regularFieldsContainer.setVisible(!isInitialSpecial);
                regularFieldsContainer.setManaged(!isInitialSpecial);

                if (isInitialSpecial) {
                    specialButton.setSelected(true);
                } else {
                    regularButton.setSelected(true);
                }

                break;
        }

        // button
        Button saveButton = new Button("💾 Save Agenda");
        saveButton.getStyleClass().add("primary-button");
        saveButton.setOnAction(e -> handleSave());

        Button backButton = new Button("← Dashboard");
        backButton.getStyleClass().add("secondary-button");
        backButton.setOnAction(e -> MainApp.showDashboardScreen());

        HBox buttonBox = new HBox(10, backButton, saveButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getStyleClass().add("form-actions");

        rootLayout.getChildren().addAll(title, messageLabel, formLayout, buttonBox);
        rootLayout.setAlignment(Pos.TOP_CENTER);

        VBox.setMargin(formLayout, new Insets(0, 50, 0, 50));
        VBox.setMargin(buttonBox, new Insets(10, 50, 0, 50));
    }

    private void addFormField(GridPane grid, Label label, Control control, int row) {
        label.getStyleClass().add("form-label");

        if (control instanceof DatePicker) {
            control.getStyleClass().add("date-picker");
        } else if (control instanceof ComboBox) {
            control.getStyleClass().add("combo-box");
        } else if (control instanceof TextField) {
            control.getStyleClass().add("text-field");
        } else if (control instanceof TextArea) {
            control.getStyleClass().add("text-area");
        }
        grid.add(label, 0, row);
        grid.add(control, 1, row);
        GridPane.setHgrow(control, javafx.scene.layout.Priority.ALWAYS);
    }

    private HBox createTypeSwitchButtons() {
        specialButton = new ToggleButton("Special");
        regularButton = new ToggleButton("Regular");

        ToggleGroup group = new ToggleGroup();
        specialButton.setToggleGroup(group);
        regularButton.setToggleGroup(group);

        specialButton.getStyleClass().add("toggle-type-button");
        regularButton.getStyleClass().add("toggle-type-button");

        specialButton.setOnAction(e -> handleTypeSwitching("Special"));
        regularButton.setOnAction(e -> handleTypeSwitching("Regular"));

        HBox box = new HBox(10, regularButton, specialButton);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private void handleTypeSwitching(String newType) {
        if (currentAgendaType.equals(newType) && specialFieldsContainer.isVisible() == ("Special".equals(newType)))
            return;

        currentAgendaType = newType;
        showMessage("", "");

        boolean isSpecial = "Special".equals(newType);

        specialFieldsContainer.setVisible(isSpecial);
        specialFieldsContainer.setManaged(isSpecial);
        regularFieldsContainer.setVisible(!isSpecial);
        regularFieldsContainer.setManaged(!isSpecial);
    }

    private int addTaskFields(GridPane grid, int startRow) {
        taskDuePicker = new DatePicker(LocalDate.now().plusDays(1));
        taskPriorityCombo = new ComboBox<>();
        taskPriorityCombo.getItems().addAll('H', 'M', 'L');
        taskPriorityCombo.setValue('M');

        addFormField(grid, new Label("Due Date"), taskDuePicker, startRow++);
        addFormField(grid, new Label("Priority"), taskPriorityCombo, startRow++);

        return startRow;
    }

    private VBox createSpecialFieldsContainer() {
        specialDate = new DatePicker(LocalDate.now().plusDays(1));
        specialTime = createTimeSpinner(LocalTime.of(10, 0));

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        int row = 0;
        addFormField(grid, new Label("Date"), specialDate, row++);
        addFormField(grid, new Label("Time"), specialTime, row++);

        VBox container = new VBox(grid);
        return container;
    }

    private VBox createRegularFieldsContainer() {
        regularDayCombo = new ComboBox<>();
        regularDayCombo.getItems().addAll(Arrays.asList(DayOfWeek.values()));
        regularDayCombo.setValue(LocalDate.now().getDayOfWeek());
        regularTime = createTimeSpinner(LocalTime.of(9, 0));

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        int row = 0;
        addFormField(grid, new Label("Day"), regularDayCombo, row++);
        addFormField(grid, new Label("Time"), regularTime, row++);

        VBox container = new VBox(grid);
        return container;
    }

    private Spinner<LocalTime> createTimeSpinner(LocalTime initialTime) {
        Spinner<LocalTime> timeSpinner = new Spinner<>();

        SpinnerValueFactory<LocalTime> valueFactory = new SpinnerValueFactory<LocalTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            {
                setValue(initialTime);
            }

            @Override
            public void decrement(int steps) {
                setValue(getValue().minusMinutes(steps * 30));
            }

            @Override
            public void increment(int steps) {
                setValue(getValue().plusMinutes(steps * 30));
            }
        };
        timeSpinner.setValueFactory(valueFactory);
        timeSpinner.setEditable(true);
        timeSpinner.getStyleClass().addAll(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL, "text-field");
        return timeSpinner;
    }

    private void handleSave() {
        // Collect data from UI fields
        String title = titleField.getText();
        String group = groupField.getText();
        String notes = notesArea.getText();

        // Collect type-specific data
        LocalDate taskDue = (currentAgendaType.equals("Task")) ? taskDuePicker.getValue() : null;
        char taskPriority = (currentAgendaType.equals("Task")) ? taskPriorityCombo.getValue() : ' ';

        LocalDate specialDateVal = (currentAgendaType.equals("Special")) ? specialDate.getValue() : null;
        LocalTime specialTimeVal = (currentAgendaType.equals("Special")) ? specialTime.getValue() : null;

        DayOfWeek regularDayVal = (currentAgendaType.equals("Regular")) ? regularDayCombo.getValue() : null;
        LocalTime regularTimeVal = (currentAgendaType.equals("Regular")) ? regularTime.getValue() : null;

        // Call Controller to process and save
        String result = controller.handleSave(
                currentAgendaType, title, group, notes,
                taskDue, taskPriority,
                specialDateVal, specialTimeVal,
                regularDayVal, regularTimeVal);

        // Separate status and message
        String[] parts = result.split(":", 2);
        String type = parts[0];
        String message = parts[1];

        showMessage(message, type);

        // If success, clear fields
        if (type.equals("success")) {
            titleField.clear();
            groupField.clear();
            notesArea.clear();
            // Specific fields don't need clearing as they reset on UI init/switch
        }
    }

    private void showMessage(String message, String type) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().removeAll("error-label", "success-label");
        // Add CSS class based on type
        if (!type.isEmpty()) {
            messageLabel.getStyleClass().add(type + "-label");
        }
    }

    public Parent getView() {
        return rootLayout;
    }
}