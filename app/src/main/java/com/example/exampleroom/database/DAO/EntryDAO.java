package com.example.exampleroom.database.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.exampleroom.database.Entity.EntryDB;
import java.util.List;

@Dao
public interface EntryDAO {
    @Query("SELECT * FROM Entry")
    List<EntryDB> getAll();

    @Query("SELECT * FROM Entry WHERE Id = :itemId")
    EntryDB findById(int itemId);

    @Insert
    void insert(EntryDB item);

    @Update
    void update(EntryDB item);

    @Delete
    void delete(EntryDB item);

    @Query("SELECT * FROM Entry WHERE Description LIKE '%' || :search || '%' ")
    List<EntryDB> getLikeAPi(String search);
}
