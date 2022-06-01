package com.example.exampleroom.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.exampleroom.database.DAO.ItemDAO;
import com.example.exampleroom.database.Entity.Item;

@Database(entities={
        Item.class
    },version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public static AppDataBase INSTANCE;

    public abstract ItemDAO itemDAO();

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
