package com.toki.ui.components;

import javafx.application.Platform;
import javafx.scene.control.Label;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Label Component that displays real-time digital clock using multithreading.
 */
public class DigitalClock extends Label {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");

    public DigitalClock() {
        super();
        this.getStyleClass().add("digital-clock");
        startClock();
    }

    private void startClock() {
        // Initialize new thread for clock
        Thread clockThread = new Thread(() -> {
            try {
                while (true) {
                    String time = LocalTime.now().format(formatter);

                    // Important: UI Updates must be on UI Thread.
                    // Platform.runLater ensures code runs on UI Thread.
                    Platform.runLater(() -> this.setText(time));

                    // Wait 1 second before next update
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                // Thread interrupted
                System.out.println("Clock thread stopped.");
            }
        });

        // Set thread as daemon so it does not prevent app exit
        clockThread.setDaemon(true);
        clockThread.start();
    }
}