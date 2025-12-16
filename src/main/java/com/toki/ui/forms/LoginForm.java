package com.toki.ui.forms;

import com.toki.repository.UserRepositoryInterface;
import com.toki.ui.MainApp;
import com.toki.ui.controller.LoginController; // Import Controller

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginForm {

    private final Stage stage;
    private final LoginController loginController; // Menggunakan Controller

    private GridPane formLayout;
    private TextField usernameField;
    private PasswordField passwordField;
    private Label errorLabel;
    private Button loginButton;
    private Button registerButton;

    private VBox rootVBox;

    public LoginForm(Stage stage) {
        this.stage = stage;
        // Inisialisasi Controller dengan Repository
        UserRepositoryInterface userRepository = MainApp.getUserRepository();
        this.loginController = new LoginController(userRepository); 
        initializeUI();
    }
    
    private void initializeUI() {

        // ... (Bagian header, layout, input, dan button tetap sama)

        // header
        Label header = new Label("Toki Scheduler");
        header.getStyleClass().add("header-label");

        // layout
        formLayout = new GridPane();
        formLayout.setAlignment(Pos.CENTER);
        formLayout.setHgap(15);
        formLayout.setVgap(15);
        formLayout.setPadding(new Insets(30, 30, 30, 30));
        formLayout.getStyleClass().add("login-pane"); 
        
        // input
        usernameField = new TextField();
        usernameField.setPromptText("Masukkan Username");
        usernameField.getStyleClass().add("text-field");

        passwordField = new PasswordField();
        passwordField.setPromptText("Masukkan Password");
        passwordField.getStyleClass().add("password-field");

        // button
        loginButton = new Button("Login");
        loginButton.getStyleClass().add("primary-button"); 
        loginButton.setDefaultButton(true);

        registerButton = new Button("Register");
        registerButton.getStyleClass().add("secondary-button");

        // error label
        errorLabel = new Label("");
        errorLabel.getStyleClass().add("error-label");

        // layout
        formLayout.add(new Label("Username:"), 0, 0);
        formLayout.add(usernameField, 1, 0);
        formLayout.add(new Label("Password:"), 0, 1);
        formLayout.add(passwordField, 1, 1);
        
        HBox buttonBox = new HBox(10, registerButton, loginButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        formLayout.add(buttonBox, 1, 2); 
        
        formLayout.add(errorLabel, 0, 3, 2, 1);
        GridPane.setHalignment(errorLabel, Pos.CENTER.getHpos());
        
        this.rootVBox = new VBox(20);
        this.rootVBox.setAlignment(Pos.CENTER);
        this.rootVBox.getChildren().addAll(header, formLayout);
        
        // button handler
        loginButton.setOnAction(e -> handleLogin());
        registerButton.setOnAction(e -> handleRegister());
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Panggil Controller
        String errorMessage = loginController.handleLogin(username, password);

        if (errorMessage != null) {
            showError(errorMessage);
        } else {
            // Jika login berhasil, controller sudah menangani navigasi ke Dashboard.
            // Di sini, kita bisa membersihkan field jika perlu, 
            // tapi karena navigasi terjadi, tidak wajib.
        }
    }

    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Panggil Controller
        String resultMessage = loginController.handleRegister(username, password);

        if (resultMessage.contains("berhasil")) {
            showSuccess(resultMessage);
            usernameField.clear();
            passwordField.clear();
        } else {
            showError(resultMessage);
        }
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.getStyleClass().removeAll("success-label");
        errorLabel.getStyleClass().add("error-label");
    }
    
    private void showSuccess(String message) {
        errorLabel.setText(message);
        errorLabel.getStyleClass().removeAll("error-label");
        errorLabel.getStyleClass().add("success-label");
    }

    public Parent getView() {
        return rootVBox;
    }
}