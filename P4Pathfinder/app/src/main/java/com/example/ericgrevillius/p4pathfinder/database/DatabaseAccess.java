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
    void insertSession(StepSession... stepSessions);

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

    @Query("SELECT * FROM step_session_table WHERE user_id = :userID")
    List<StepSession> getUsersSessions(long userID);

    @Query("SELECT * FROM step_session_table WHERE user_id = :userID AND session_date <= :toDate")
    List<StepSession> getUsersSessionsTo(long userID, long toDate);

    @Query("SELECT * FROM step_session_table WHERE user_id = :userID AND session_date >= :fromDate")
    List<StepSession> getUsersSessionsFrom(long userID, long fromDate);

    @Query("SELECT * FROM step_session_table WHERE user_id = :userID AND session_date BETWEEN :fromDate AND :toDate")
    List<StepSession> getUsersSessionsFromTo(long userID, long fromDate, long toDate);

    @Query("SELECT count(step_id) FROM step_table WHERE session_id = :sessionID")
    int getTotalSteps(long sessionID);

    @Query("SELECT count(step_id) FROM step_table WHERE session_id = :sessionID AND step_movement = :typeMovement")
    int getTypeSteps(long sessionID, String typeMovement);

    @Query("DELETE FROM step_session_table WHERE user_id = :userID")
    void deleteAllSessions(long userID);

    @Query("DELETE FROM step_table WHERE session_id = :sessionID")
    void deleteAllSteps(long sessionID);

    @Query("SELECT * FROM step_session_table WHERE session_id = :sessionID")
    StepSession getSession(long sessionID);
}
