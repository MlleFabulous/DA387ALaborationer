package com.example.ericgrevillius.p4pathfinder.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "step_table")
public class Step {

    @ForeignKey(entity = StepSession.class, parentColumns = "session_id", childColumns = "session_id")
    @ColumnInfo(name = "session_id")
    private long sessionID;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "step_id")
    private long stepID;

    @ColumnInfo(name = "step_movement")
    private String movement;

    @ColumnInfo(name = "step_date")
    private long date;


    public Step(long sessionID, String movement, long date) {
        this.sessionID = sessionID;
        this.movement = movement;
        this.date = date;
    }

    @Ignore
    public Step(long sessionID, long stepID, String movement, long date) {
        this.sessionID = sessionID;
        this.stepID = stepID;
        this.movement = movement;
        this.date = date;
    }

    public long getSessionID() {
        return sessionID;
    }

    public void setSessionID(long sessionID) {
        this.sessionID = sessionID;
    }

    public long getStepID() {
        return stepID;
    }


    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
