package com.example.ericgrevillius.p4pathfinder.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "step_session_table")
public class StepSession {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "session_id")
    private long sessionID;

    @ColumnInfo(name = "user_id")
    private long userID;

    @ColumnInfo(name = "session_date")
    private long date;

    private int steps;
    private int walkedSteps;
    private int runSteps;


    public StepSession(long sessionID, long userID, long date) {
        this.sessionID = sessionID;
        this.userID = userID;
        this.date = date;
    }

    @Ignore
    public StepSession(long userID, long date) {
        this.userID = userID;
        this.date = date;
    }

    public long getSessionID() {
        return sessionID;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public long getDate() {
        return date;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getWalkedSteps() {
        return walkedSteps;
    }

    public void setWalkedSteps(int walkedSteps) {
        this.walkedSteps = walkedSteps;
    }

    public int getRunSteps() {
        return runSteps;
    }

    public void setRunSteps(int ranSteps) {
        this.runSteps = ranSteps;
    }
}
