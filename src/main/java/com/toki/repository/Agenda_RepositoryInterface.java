package com.toki.repository;

import java.util.List;

public interface Agenda_RepositoryInterface<T> {
    void save(T item);
    T findByID(int ID); 
    List<T> findAll();
    void deleteByID(int ID);
}