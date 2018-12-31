package com.example.ericgrevillius.p4pathfinder.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "user_table")
public class User {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    private long userID;

    @ColumnInfo(name = "username")
    private String username;

    @ColumnInfo(name = "password")
    private String password;

    @ColumnInfo(name = "fingerprint_login")
    private boolean fingerprintLogin;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public long getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFingerprintLogin(boolean fingerprintLogin) {
        this.fingerprintLogin = fingerprintLogin;
    }

    public boolean hasFingerprintLogin() {
        return fingerprintLogin;
    }
}
