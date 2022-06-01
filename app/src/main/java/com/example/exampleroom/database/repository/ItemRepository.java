package com.example.exampleroom.database.repository;
import com.example.exampleroom.database.Entity.Item;
import java.util.List;

public interface ItemRepository {
    List<Item> getAll();
    Item getById(int id);
    void insert(Item item);
    void update(Item item);
    void delete(Item item);
    List<Item> getLikeName(String search);
}
