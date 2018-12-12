package com.example.ericgrevillius.p4pathfinder.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "step_table")
public class Step {
    @ForeignKey(entity = User.class, parentColumns = "user_id", childColumns = "user_id")
    @ColumnInfo(name = "user_id")
    private long userID;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "step_id")
    private long stepID;

    @ColumnInfo(name = "step_count")
    private int stepCount;

    @ColumnInfo(name = "step_date")
    private String date;

    public Step(long userID, int stepCount, String date) {
        this.userID = userID;
        this.stepCount = stepCount;
        this.date = date;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public long getStepID() {
        return stepID;
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
