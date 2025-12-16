package com.toki.ui;

import com.toki.model.User;
import com.toki.repository.*;
import com.toki.service.SchedulerService;
import com.toki.ui.forms.AgendaForm;
import com.toki.ui.forms.LoginForm;
import com.toki.ui.view.DashboardView;
import com.toki.ui.util.CssManager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static SchedulerService schedulerService;
    private static UserRepositoryInterface userRepository;
    private static Stage primaryStage;
    private static User currentUser;
    private static DashboardView dashboardView;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        // Repository Initialization
        Agenda_RepositoryInterface regularRepo = new AgendaRegularRepository();
        Agenda_RepositoryInterface specialRepo = new AgendaSpecialRepository();
        Agenda_RepositoryInterface taskRepo = new AgendaTaskRepository();
        userRepository = new UserRepository();

        schedulerService = new SchedulerService(regularRepo, specialRepo, taskRepo);

        showLoginScreen();

    }

    /**
     * Shows the Login Screen.
     */
    public static void showLoginScreen() {
        LoginForm loginForm = new LoginForm(primaryStage);
        Scene scene = new Scene(loginForm.getView(), 400, 400);

        CssManager.apply(scene, "/css/login.css");

        primaryStage.setTitle("Toki Scheduler - Login");
        primaryStage.setMaximized(false);
        primaryStage.setFullScreen(false);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    /**
     * Shows the Dashboard Screen.
     */
    public static void showDashboardScreen() {
        // Pass dependencies to DashboardView
        dashboardView = new DashboardView(primaryStage, schedulerService, currentUser);
        Scene scene = new Scene(dashboardView.getView(), 1440, 810);
        CssManager.apply(scene, "/css/dashboard.css");

        primaryStage.setTitle("Toki Scheduler");
        primaryStage.setScene(scene);

        // Maximize the window
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    /**
     * Shows the Agenda Form for a specific type.
     * 
     * @param agendaType The type of agenda to create ("Task", "Regular", etc.)
     */
    public static void showAgendaForm(String agendaType) {
        AgendaForm agendaForm = new AgendaForm(primaryStage, agendaType);
        Scene scene = new Scene(agendaForm.getView(), 1440, 810);
        CssManager.apply(scene, "/css/agendaForm.css");

        primaryStage.setTitle("Toki Scheduler");
        primaryStage.setScene(scene);

        // Maximize the window
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void showAgendaList() {
        // Implementation for showing list view
    }

    public static SchedulerService getSchedulerService() {
        return schedulerService;
    }

    public static UserRepositoryInterface getUserRepository() {
        return userRepository;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }
}
