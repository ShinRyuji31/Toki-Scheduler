// File: com.toki.repository.AgendaTaskRepository.java (Revisi Total)

package com.toki.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import com.toki.model.AgendaTask;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AgendaTaskRepository implements Agenda_RepositoryInterface<AgendaTask> {

    private static final String FILE_PATH = "data/database/task.json";
    private final Gson gson;
    private final Type taskListType;

    private static class LocalDateAdapter extends TypeAdapter<LocalDate> {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

        @Override
        public void write(JsonWriter out, LocalDate value) throws IOException {
            out.value(value.format(FORMATTER));
        }

        @Override
        public LocalDate read(JsonReader in) throws IOException {
            return LocalDate.parse(in.nextString(), FORMATTER);
        }
    }
    
    private static class LocalTimeAdapter extends TypeAdapter<LocalTime> {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss"); 

        @Override
        public void write(JsonWriter out, LocalTime value) throws IOException {
            out.value(value.format(FORMATTER));
        }

        @Override
        public LocalTime read(JsonReader in) throws IOException {
            return LocalTime.parse(in.nextString(), FORMATTER);
        }
    }

    public AgendaTaskRepository() {
        this.gson = new GsonBuilder()
                        .setPrettyPrinting()
                        .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                        .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                        .create();
                        
        this.taskListType = new TypeToken<List<AgendaTask>>() {}.getType();
        
        File file = new File(FILE_PATH);
        File parentDir = file.getParentFile();
        
        // 2. Buat direktori jika belum ada
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        
        // 3. FIX EOFException: Jamin file ada dan berisi "[]" jika kosong
        if (!file.exists() || file.length() == 0) {
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                // Tulis array kosong ke file
                saveAll(new ArrayList<>()); 
                System.out.println("✅ Task data file initialized: " + FILE_PATH);
            } catch (IOException e) {
                System.err.println("❌ Error initializing Task data file: " + e.getMessage());
            }
        }
    }

    @Override
    public List<AgendaTask> findAll() {
        File file = new File(FILE_PATH);
        
        // Setelah inisialisasi di constructor, file pasti ada dan tidak kosong (minimal "[]")
        // Namun, kita tetap menjaga pengecekan untuk berjaga-jaga
        if (!file.exists()) { 
            return new ArrayList<>();
        }
        
        try (Reader reader = new FileReader(file)) {
            List<AgendaTask> tasks = gson.fromJson(reader, taskListType);
            return tasks != null ? tasks : new ArrayList<>();
        } catch (IOException e) {
            // Ini akan menangkap jika file hilang setelah constructor dipanggil
            e.printStackTrace();
            return new ArrayList<>();
        } catch (com.google.gson.JsonSyntaxException e) {
             // Tambahkan penanganan jika file menjadi rusak (corrupt)
             System.err.println("❌ Data file corrupted. Returning empty list: " + e.getMessage());
             e.printStackTrace();
             return new ArrayList<>();
        }
    }

    private void saveAll(List<AgendaTask> tasks) {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(tasks, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(AgendaTask newTask) {
        List<AgendaTask> tasks = findAll();
        
        // Filter out existing task by ID (Update logic)
        List<AgendaTask> filteredTasks = tasks.stream()
            .filter(t -> t.getID() != newTask.getID())
            .collect(Collectors.toList());
        
        filteredTasks.add(newTask);
        saveAll(filteredTasks);
    }
    
    @Override
    public AgendaTask findByID(int ID) {
        return findAll().stream()
            .filter(task -> task.getID() == ID)
            .findFirst()
            .orElse(null);
    }

    @Override
    public void deleteByID(int ID) {
        List<AgendaTask> tasks = findAll();
        List<AgendaTask> updatedList = tasks.stream()
            .filter(t -> t.getID() != ID)
            .collect(Collectors.toList());
        saveAll(updatedList);
    }
}