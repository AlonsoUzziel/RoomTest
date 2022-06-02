package com.example.exampleroom.database.repository;

import com.example.exampleroom.database.Entity.EntryDB;

import java.util.List;

public interface EntryRepository {
    List<EntryDB> getAll();
    EntryDB getById(int id);
    void insert(EntryDB item);
    void update(EntryDB item);
    void delete(EntryDB item);
    List<EntryDB> getLikeApi(String search);
}
