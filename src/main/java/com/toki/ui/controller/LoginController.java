package com.toki.ui.controller;

import com.toki.model.User;
import com.toki.repository.UserRepositoryInterface;
import com.toki.ui.MainApp;

/**
 * Controller for handling User Logic/Authentication.
 */
public class LoginController {

    private final UserRepositoryInterface userRepository;

    public LoginController(UserRepositoryInterface userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Handles the user login process.
     * 
     * @param username The entered username.
     * @param password The entered password.
     * @return Error message/null if login successful.
     */
    public String handleLogin(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            return "Username and password cannot be empty.";
        }

        User user = userRepository.findByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            System.out.println("Login Successful for: " + user.getUsername());
            MainApp.setCurrentUser(user);
            // Move to dashboard after successful login
            MainApp.showDashboardScreen();
            return null; // Login successful
        } else {
            return "Invalid username or password.";
        }
    }

    /**
     * Handles the new user registration process.
     * 
     * @param username The entered username.
     * @param password The entered password.
     * @return Status message (success or error).
     */
    public String handleRegister(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            return "Please fill in all fields to register.";
        }

        if (userRepository.findByUsername(username) != null) {
            return "Username is already taken.";
        }

        User newUser = new User(username, password);
        userRepository.save(newUser);

        return "Registration successful! Please login.";
    }
}