package com.example.ericgrevillius.p4pathfinder.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface DatabaseAccess {
    //TODO: Create the access methods for the database.

    @Insert
    void insertUser(User... users);

    @Insert
    void insertStep(Step... steps);

    @Update
    void updateUser(User user);

    @Query("SELECT * FROM user_table WHERE username = :username;")
    User getUser(String username);

    @Query("SELECT * FROM user_table WHERE fingerprint_login = :hasFingerprint")
    List<User> getUserFingerprintLogin(boolean hasFingerprint);

    @Query("SELECT * FROM user_table;")
    List<User> getAllUsers();

//    @Query("SELECT * FROM step_table WHERE user_id = :userID")
}
