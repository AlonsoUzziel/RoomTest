package com.example.exampleroom.database.repository;

import com.example.exampleroom.database.DAO.ItemDAO;
import com.example.exampleroom.database.Entity.Item;

import java.util.List;

public class ItemRepositoryImpl implements ItemRepository{
    ItemDAO dao;

    public ItemRepositoryImpl(ItemDAO dao){
        this.dao = dao;
    }

    @Override
    public List<Item> getAll() {
        return dao.getAll();
    }

    @Override
    public Item getById(int id) {
        return dao.findById(id);
    }

    @Override
    public void insert(Item item) {
        dao.insert(item);
    }

    @Override
    public void update(Item item) {
        dao.update(item);
    }

    @Override
    public void delete(Item item) {
        dao.delete(item);
    }

    @Override
    public List<Item> getLikeName(String search){
        return  dao.getLikeName(search);
    }
}
