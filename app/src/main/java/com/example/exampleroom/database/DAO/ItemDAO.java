package com.example.exampleroom.database.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.exampleroom.database.Entity.Item;

import java.util.List;

@Dao
public interface ItemDAO {
    @Query("SELECT * FROM Item")
    List<Item> getAll();

    @Query("SELECT * FROM Item WHERE Id = :itemId")
    Item findById(int itemId);

    @Query("SELECT * FROM Item WHERE name LIKE '%' || :search_query || '%' ")
    List<Item> getLikeName(String search_query);

    @Insert
    void insert(Item item);

    @Update
    void update(Item item);

    @Delete
    void delete(Item item);
}
