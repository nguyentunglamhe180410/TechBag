package com.example.techbag.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.techbag.Dao.ItemsDAO;
import com.example.techbag.Models.Items;

@Database(entities = Items.class, version = 1, exportSchema = false)
public abstract class RoomDb extends RoomDatabase {
    private static RoomDb database;
    private static String DATABASE_NAME = "TechBagDB";

    public synchronized static RoomDb getInstance(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context.getApplicationContext(), RoomDb.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return database;
    }
    public abstract ItemsDAO mainDao();
}
