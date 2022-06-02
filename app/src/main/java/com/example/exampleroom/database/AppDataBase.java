package com.example.exampleroom.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.exampleroom.database.DAO.EntryDAO;
import com.example.exampleroom.database.DAO.ItemDAO;
import com.example.exampleroom.database.Entity.Item;
import com.example.exampleroom.database.Entity.EntryDB;

@Database(entities={
        Item.class,
        EntryDB.class
    },version = 2)
public abstract class AppDataBase extends RoomDatabase {
    public static AppDataBase INSTANCE;

    public abstract ItemDAO itemDAO();
    public abstract EntryDAO entryDAO();

    public static AppDataBase getInstance(Context context)
    {
        if (INSTANCE == null)
        {
            INSTANCE = Room.databaseBuilder(context,AppDataBase.class,"checklist.db")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return INSTANCE;
    }

}
