package com.toki.ui.view;

import com.toki.model.AgendaAbstract;
import com.toki.model.AgendaRegular;
import com.toki.model.AgendaSpecial;
import com.toki.model.AgendaTask;
import com.toki.ui.util.CssManager;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * View for displaying details of a specific Agenda.
 * Shows title, type, date/time, and other specific properties.
 */
public class AgendaDetailsView {

    /**
     * Shows a popup window with the details of the given agenda.
     * 
     * @param agenda The agenda to display.
     */
    public static void show(AgendaAbstract agenda) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Agenda Details");

        VBox root = new VBox();
        root.getStyleClass().add("details-root");

        // Title
        Label titleLabel = new Label(agenda.getTitle());
        titleLabel.getStyleClass().add("details-title");

        root.getChildren().add(titleLabel);

        // Common Details
        addDetail(root, "Type:", agenda.getType());
        addDetail(root, "Group:", agenda.getGroup());
        addDetail(root, "Notes:", agenda.getNotes());

        // Specific Details
        if (agenda instanceof AgendaRegular regular) {
            addDetail(root, "Day:", regular.getDay().toString());
            addDetail(root, "Time:", regular.getTime().toString());
        } else if (agenda instanceof AgendaSpecial special) {
            addDetail(root, "Date:", special.getDate().toString());
            addDetail(root, "Time:", special.getTime().toString());
        } else if (agenda instanceof AgendaTask task) {
            addDetail(root, "Due Date:", task.getDue().toString());
            addDetail(root, "Priority:", String.valueOf(task.getPriority()));
        }

        Button closeButton = new Button("Close");
        closeButton.getStyleClass().add("close-button");
        closeButton.setOnAction(e -> stage.close());

        VBox buttonContainer = new VBox(closeButton);
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
        root.getChildren().add(buttonContainer);

        Scene scene = new Scene(root, 300, 400);
        CssManager.apply(scene, "/css/agendaDetail.css");

        stage.setScene(scene);
        stage.centerOnScreen();
        stage.showAndWait();
    }

    private static void addDetail(VBox parent, String label, String value) {
        if (value == null || value.isEmpty())
            return;

        VBox section = new VBox();
        section.getStyleClass().add("details-section");

        Label l = new Label(label);
        l.getStyleClass().add("details-label");

        Label v = new Label(value);
        v.getStyleClass().add("details-value");
        v.setWrapText(true);

        section.getChildren().addAll(l, v);
        parent.getChildren().add(section);
    }
}
