package com.example.exampleroom.database.repository;

import com.example.exampleroom.database.DAO.EntryDAO;
import com.example.exampleroom.database.Entity.EntryDB;

import java.util.List;

public class EntryRepositoryImpl implements EntryRepository{
    EntryDAO dao;

    public EntryRepositoryImpl(EntryDAO dao){
        this.dao = dao;
    }

    @Override
    public List<EntryDB> getAll() {
        return dao.getAll();
    }

    @Override
    public EntryDB getById(int id) {
        return dao.findById(id);
    }

    @Override
    public void insert(EntryDB item) {
        dao.insert(item);
    }

    @Override
    public void update(EntryDB item) {
        dao.update(item);
    }

    @Override
    public void delete(EntryDB item) {
        dao.delete(item);
    }

    @Override
    public List<EntryDB> getLikeApi(String search){
        return dao.getLikeAPi(search);
    }
}
