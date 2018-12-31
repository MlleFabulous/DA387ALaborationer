package com.example.ericgrevillius.p4pathfinder.database;


import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@android.arch.persistence.room.Database(entities = {User.class, Step.class}, version = 1, exportSchema = false)
public abstract class Database extends RoomDatabase {
    private static final String TAG = "Database";
    public abstract DatabaseAccess databaseAccess();

    private static volatile Database INSTANCE;

    public static Database getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (Database.class){
                INSTANCE  = Room.databaseBuilder(context, Database.class, TAG)
                        .fallbackToDestructiveMigration()
                        .build();

            }
        }
        return INSTANCE;
    }}
