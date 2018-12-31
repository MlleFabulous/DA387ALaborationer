package com.example.ericgrevillius.p4pathfinder;

import android.content.Context;

import com.example.ericgrevillius.p4pathfinder.database.Database;
import com.example.ericgrevillius.p4pathfinder.database.DatabaseAccess;
import com.example.ericgrevillius.p4pathfinder.database.User;

import java.util.List;

public class DatabaseController {
    private Database database;
    private DatabaseAccess databaseAccess;
    private ThreadPool threadPool;
    private DatabaseLoginListener databaseLoginListener;

    public DatabaseController(Context context){
        database = Database.getDatabase(context);
        databaseAccess = database.databaseAccess();
        threadPool = new ThreadPool(1);
        threadPool.start();
    }

    public void stop(){
        database = null;
        databaseAccess = null;
        threadPool.stop();
    }

    public void setDatabaseLoginListener(DatabaseLoginListener databaseLoginListener) {
        this.databaseLoginListener = databaseLoginListener;
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
            List<User> users = databaseAccess.getUserFingerprintLogin(true);
            if (users != null) {
                databaseLoginListener.resultFingerprintLogin(users.get(0));
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
        @Override
        public void run() {

        }
    }

    private class UpdateSessionTask implements Runnable{
        @Override
        public void run() {

        }
    }

    private class DeleteSessionTask implements Runnable{
        @Override
        public void run() {

        }
    }

    private class InsertStepTask implements Runnable{
        @Override
        public void run() {

        }
    }

    private class UpdateStepTask implements Runnable{
        @Override
        public void run() {

        }
    }

    private class DeleteStepTask implements Runnable{
        @Override
        public void run() {

        }
    }

    public interface DatabaseLoginListener {
        void resultFingerprintLogin(boolean available);
        void resultUsername(boolean available);
        void resultLogin(String result);
        void resultFingerprintLogin(User user);
    }
}
