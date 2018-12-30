package com.example.ericgrevillius.p4pathfinder.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "step_session_table")
public class StepSession {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "session_id")
    private long sessionID;

    @ColumnInfo(name = "user_id")
    private long userID;

}
