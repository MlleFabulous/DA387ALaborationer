package com.example.ericgrevillius.p4pathfinder.database;


@android.arch.persistence.room.Database(entities = {User.class, Step.class}, version = 1, exportSchema = false)
public abstract class Database {
    public abstract DatabaseAccess databaseAccess();
}
