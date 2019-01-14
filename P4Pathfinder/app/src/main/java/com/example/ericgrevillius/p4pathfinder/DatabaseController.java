package com.example.ericgrevillius.p4pathfinder;

import android.content.Context;

import com.example.ericgrevillius.p4pathfinder.database.Database;
import com.example.ericgrevillius.p4pathfinder.database.DatabaseAccess;
import com.example.ericgrevillius.p4pathfinder.database.Step;
import com.example.ericgrevillius.p4pathfinder.database.StepSession;
import com.example.ericgrevillius.p4pathfinder.database.User;

import java.util.Calendar;
import java.util.List;

public class DatabaseController {
    private static DatabaseController instance;
    private Database database;
    private DatabaseAccess databaseAccess;
    private ThreadPool threadPool;
    private DatabaseLoginListener databaseLoginListener;
    private DatabaseServiceListener databaseServiceListener;
    private DatabaseUIListener databaseUIListener;

    public static DatabaseController getInstance(Context context){
        if (instance == null){
            instance = new DatabaseController(context);
        }
        return instance;
    }

    private DatabaseController(Context context){
        database = Database.getDatabase(context);
        databaseAccess = database.databaseAccess();
        threadPool = new ThreadPool(1);
        threadPool.start();
    }

    public void stop(){
        database = null;
        databaseAccess = null;
        threadPool.stop();
        instance = null;
    }

    public void setDatabaseLoginListener(DatabaseLoginListener databaseLoginListener) {
        this.databaseLoginListener = databaseLoginListener;
    }

    public void setDatabaseServiceListener(DatabaseServiceListener databaseServiceListener) {
        this.databaseServiceListener = databaseServiceListener;
    }

    public void setDatabaseUIListener(DatabaseUIListener databaseUIListener) {
        this.databaseUIListener = databaseUIListener;
    }

    public void insertUser(String username, String password, boolean fingerprintLogin){
        InsertUserTask task = new InsertUserTask(username, password, fingerprintLogin);
        threadPool.execute(task);
    }

    public void checkFingerprintLoginAvailability(){
        CheckFingerprintLoginAvailabilityTask task = new CheckFingerprintLoginAvailabilityTask();
        threadPool.execute(task);
    }

    public void checkUsernameAvailability(String username) {
        CheckUsernameAvailabilityTask task = new CheckUsernameAvailabilityTask(username);
        threadPool.execute(task);
    }

    public void getUserWithFingerprint() {
        GetUserWithFingerprintTask task = new GetUserWithFingerprintTask();
        threadPool.execute(task);
    }

    public void login(String username, String password) {
        LoginTask task = new LoginTask(username, password);
        threadPool.execute(task);
    }

    public void getUserForService(String username) {
        GetUserForServiceTask task = new GetUserForServiceTask(username);
        threadPool.execute(task);
    }

    public void insertStepSession(long userID) {
        InsertSessionTask task = new InsertSessionTask(userID);
        threadPool.execute(task);
    }

    public void insertStep(Step step) {
        InsertStepTask task = new InsertStepTask(step);
        threadPool.execute(task);
    }

    public void deleteAllSession(String username) {
        DeleteAllSessionsTask task = new DeleteAllSessionsTask(username);
        threadPool.execute(task);
    }

    public void getSessionsBetween(String username, long fromDate, long toDate) {
        GetSessionsBetween task = new GetSessionsBetween(username, fromDate, toDate);
        threadPool.execute(task);
    }

    public void getSessionInformation(long sessionID) {
        GetSessionInformationTask task = new GetSessionInformationTask(sessionID);
        threadPool.execute(task);
    }

    private class InsertUserTask implements Runnable{
        private String username;
        private String password;
        private boolean hasFingerprintLogin;

        public InsertUserTask(String username, String password, boolean fingerprintLogin) {
            this.username = username;
            this.password = password;
            this.hasFingerprintLogin = fingerprintLogin;
        }

        @Override
        public void run() {
            User user = new User(username,password);
            user.setFingerprintLogin(hasFingerprintLogin);
            databaseAccess.insertUser(user);
        }
    }

    private class CheckFingerprintLoginAvailabilityTask implements Runnable{
        @Override
        public void run() {
            List<User> users = databaseAccess.getUserFingerprintLogin(true);
            if (users == null || users.isEmpty() ){
                databaseLoginListener.resultFingerprintLogin(true);
            } else {
                databaseLoginListener.resultFingerprintLogin(false);
            }
        }
    }

    private class CheckUsernameAvailabilityTask implements Runnable{
        private String username;

        public CheckUsernameAvailabilityTask(String username) {
            this.username = username;
        }

        @Override
        public void run() {
            User user = databaseAccess.getUser(username);
            if (user == null){
                databaseLoginListener.resultUsername(true);
            } else {
                databaseLoginListener.resultUsername(false);
            }
        }
    }

    private class GetUserWithFingerprintTask implements Runnable{
        @Override
        public void run() {
            try{
                List<User> users = databaseAccess.getUserFingerprintLogin(true);
                if (users != null) {
                    databaseLoginListener.resultFingerprintLogin(users.get(0));
                }
            } catch (IndexOutOfBoundsException e){
                e.printStackTrace();
                databaseLoginListener.resultFingerprintLogin(null);
            }
        }
    }

    private class LoginTask implements Runnable{
        private String username;
        private String password;

        public LoginTask(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        public void run() {
            User user = databaseAccess.getUser(username);
            if (user != null){
                if (user.getPassword().equals(password)){
                    databaseLoginListener.resultLogin("Success");
                } else {
                    databaseLoginListener.resultLogin("Wrong password");
                }
            } else {
                databaseLoginListener.resultLogin("No user");
            }
        }
    }

    private class GetUserForServiceTask implements Runnable{
        private String username;
        public GetUserForServiceTask(String username) {
            this.username = username;
        }

        @Override
        public void run() {
            User user = databaseAccess.getUser(username);
            if (user != null){
                databaseServiceListener.setUser(user);
            }
        }
    }

    private class UpdateUserTask implements Runnable{
        @Override
        public void run() {

        }
    }

    private class DeleteUserTask implements Runnable{
        @Override
        public void run() {

        }
    }

    private class InsertSessionTask implements Runnable{
        private final long userID;

        public InsertSessionTask(long userID) {
            this.userID = userID;
        }

        @Override
        public void run() {
            Calendar calendar = Calendar.getInstance();
            StepSession session = new StepSession(userID, calendar.getTimeInMillis());
            databaseAccess.insertSession(session);
            List <StepSession> sessions = databaseAccess.getUsersSessions(userID);
            session = sessions.get(sessions.size()-1);
            databaseServiceListener.setSession(session);
        }
    }

    private class InsertStepTask implements Runnable{
        private final Step step;

        public InsertStepTask(Step step) {
            this.step = step;
        }

        @Override
        public void run() {
            databaseAccess.insertStep(step);
        }
    }

    private class DeleteAllSessionsTask implements Runnable {
        private String username;

        public DeleteAllSessionsTask(String username) {
            this.username = username;
        }

        @Override
        public void run() {
            User user = databaseAccess.getUser(username);
            List<StepSession> sessionList = databaseAccess.getUsersSessions(user.getUserID());
            for (StepSession s: sessionList) {
                databaseAccess.deleteAllSteps(s.getSessionID());
            }
            databaseAccess.deleteAllSessions(user.getUserID());
        }
    }

    private class GetSessionsBetween implements Runnable{
        private String username;
        private long fromDate;
        private long toDate;

        public GetSessionsBetween(String username, long fromDate, long toDate) {
            this.username = username;
            this.fromDate = fromDate;
            this.toDate = toDate;
        }

        @Override
        public void run() {
            User user = databaseAccess.getUser(username);
            List<StepSession> sessions;
            if (fromDate == -1 && toDate == -1){
                //use neither
                sessions = databaseAccess.getUsersSessions(user.getUserID());
            } else if (fromDate == -1){
                //use toDate
                sessions = databaseAccess.getUsersSessionsTo(user.getUserID(), toDate);
            } else if (toDate == -1){
                //use fromDate
                sessions = databaseAccess.getUsersSessionsFrom(user.getUserID(), fromDate);
            } else {
                //use both
                sessions = databaseAccess.getUsersSessionsFromTo(user.getUserID(), fromDate, toDate);
            }
            for (StepSession s: sessions) {
                int steps = databaseAccess.getTotalSteps(s.getSessionID());
                s.setSteps(steps);
            }
            databaseUIListener.setSessionList(sessions);
        }

    }

    private class GetSessionInformationTask implements Runnable{
        private long sessionID;
        public GetSessionInformationTask(long sessionID) {
            this.sessionID = sessionID;
        }

        @Override
        public void run() {
            StepSession session = databaseAccess.getSession(sessionID);
            session.setSteps(databaseAccess.getTotalSteps(sessionID));
            session.setWalkedSteps(databaseAccess.getTypeSteps(sessionID, "walking"));
            session.setRunSteps(databaseAccess.getTypeSteps(sessionID,"running"));
            databaseUIListener.setSessionInformation(session);
        }
    }

    public interface DatabaseServiceListener{
        void setUser(User user);
        void setSession(StepSession stepSession);
    }

    public interface DatabaseUIListener{
        void setSessionList(List<StepSession> list);
        void setSessionInformation(StepSession stepSession);
    }

    public interface DatabaseLoginListener {
        void resultFingerprintLogin(boolean available);
        void resultUsername(boolean available);
        void resultLogin(String result);
        void resultFingerprintLogin(User user);

    }
}