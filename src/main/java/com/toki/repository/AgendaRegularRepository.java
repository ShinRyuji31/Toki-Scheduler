package com.toki.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.toki.model.AgendaRegular;

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

public class AgendaRegularRepository implements Agenda_RepositoryInterface<AgendaRegular> {

    private static final String FILE_PATH = "data/database/regular.json"; 
    private final Gson gson;
    private final Type agendaListType;

    public AgendaRegularRepository() {
        this.gson = new GsonBuilder()
                        .setPrettyPrinting()
                        .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                        .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                        .create();
                        
        this.agendaListType = new TypeToken<List<AgendaRegular>>() {}.getType();
        
        File file = new File(FILE_PATH);
        File parentDir = file.getParentFile();
        
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
    }

    @Override
    public List<AgendaRegular> findAll() {
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        try (Reader reader = new FileReader(file)) {
            List<AgendaRegular> agendas = gson.fromJson(reader, agendaListType);
            return agendas != null ? agendas : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void saveAll(List<AgendaRegular> agendas) {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(agendas, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(AgendaRegular newAgenda) {
        List<AgendaRegular> agendas = findAll();
        
        // Filter out existing agenda by ID (Update logic)
        List<AgendaRegular> filteredAgendas = agendas.stream()
            .filter(a -> a.getID() != newAgenda.getID()) // <<< REFACTOR: Use getId()
            .collect(Collectors.toList());
        
        filteredAgendas.add(newAgenda);
        saveAll(filteredAgendas);
    }

    @Override
    public AgendaRegular findByID(int ID) { // <<< REFACTOR: Use int ID
        return findAll().stream()
            .filter(agenda -> agenda.getID() == ID) // <<< REFACTOR: Use getId()
            .findFirst()
            .orElse(null);
    }

    @Override
    public void deleteByID(int ID) { // <<< REFACTOR: Use int ID
        List<AgendaRegular> agendas = findAll();
        List<AgendaRegular> updatedList = agendas.stream()
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