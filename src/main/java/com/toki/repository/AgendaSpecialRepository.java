package com.toki.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.toki.model.AgendaSpecial; // Menggunakan AgendaSpecial

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Repository implementation for Special Agendas.
 * Manages in-memory storage of AgendaSpecial objects.
 */
public class AgendaSpecialRepository implements Agenda_RepositoryInterface<AgendaSpecial> {

    private static final String FILE_PATH = "data/database/special.json";
    private final Gson gson;
    private final Type agendaListType;

    public AgendaSpecialRepository() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter()) // Tambahkan juga
                .create();

        this.agendaListType = new TypeToken<List<AgendaSpecial>>() {
        }.getType();

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
    public List<AgendaSpecial> findAll() {
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        try (Reader reader = new FileReader(file)) {
            List<AgendaSpecial> agendas = gson.fromJson(reader, agendaListType);
            return agendas != null ? agendas : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void saveAll(List<AgendaSpecial> agendas) {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(agendas, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(AgendaSpecial newAgenda) {
        List<AgendaSpecial> agendas = findAll();

        // Filter out existing agenda by ID (Update logic)
        List<AgendaSpecial> filteredAgendas = agendas.stream()
                .filter(a -> a.getID() != newAgenda.getID()) // <<< REFACTOR: Use getId()
                .collect(Collectors.toList());

        filteredAgendas.add(newAgenda);
        saveAll(filteredAgendas);
    }

    @Override
    public AgendaSpecial findByID(int ID) { // <<< REFACTOR: Use int ID
        return findAll().stream()
                .filter(agenda -> agenda.getID() == ID) // <<< REFACTOR: Use getId()
                .findFirst()
                .orElse(null);
    }

    @Override
    public void deleteByID(int ID) { // <<< REFACTOR: Use int ID
        List<AgendaSpecial> agendas = findAll();
        List<AgendaSpecial> updatedList = agendas.stream()
                .filter(a -> a.getID() != ID) // <<< REFACTOR: Use getId()
                .collect(Collectors.toList());
        if (updatedList.size() < agendas.size()) {
            saveAll(updatedList);
        }
    }

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
}