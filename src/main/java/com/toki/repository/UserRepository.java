package com.toki.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.toki.model.User;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserRepository implements UserRepositoryInterface {

    private static final String FILE_PATH = "data/database/users.json"; 
    private final Gson gson;
    private final Type userListType;

    public UserRepository() {
        this.gson = new GsonBuilder().setPrettyPrinting().create(); 
        this.userListType = new TypeToken<List<User>>() {}.getType();
        
        File file = new File(FILE_PATH);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        if (!file.exists()) {
            saveAll(new ArrayList<>());
        }
    }
    public List<User> findAll() {
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        try (Reader reader = new FileReader(file)) {
            List<User> users = gson.fromJson(reader, userListType);
            return users != null ? users : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Error reading user data: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void saveAll(List<User> users) {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            System.err.println("Error writing user data: " + e.getMessage());
        }
    }

    @Override
    public void save(User newUser) {
        List<User> users = findAll();
        
        Optional<User> existingUser = users.stream()
            .filter(u -> u.getUsername().equals(newUser.getUsername()))
            .findFirst();
        
        if (existingUser.isPresent()) {
            List<User> filteredUsers = users.stream()
                .filter(u -> !u.getUsername().equals(newUser.getUsername()))
                .collect(Collectors.toList());
            filteredUsers.add(newUser);
            saveAll(filteredUsers);
        } else {
            users.add(newUser);
            saveAll(users);
        }
    }

    @Override
    public User findByUsername(String username) {
        return findAll().stream()
            .filter(user -> user.getUsername().equals(username))
            .findFirst()
            .orElse(null);
    }
}