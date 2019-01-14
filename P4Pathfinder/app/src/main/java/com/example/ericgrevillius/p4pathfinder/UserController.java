package com.example.ericgrevillius.p4pathfinder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.ericgrevillius.p4pathfinder.database.StepSession;

import java.util.List;

public class UserController implements DatabaseController.DatabaseUIListener {
    private static final String TAG = "UserController";
    private static final String WELCOME_FRAGMENT_TAG = "WelcomeFragment";
    private static final String CURRENT_SESSION_FRAGMENT_TAG = "CurrentSessionFragment";
    private static final String SESSION_HISTORY_FRAGMENT_TAG = "SessionHistoryFragment";
    private static final String ACCOUNT_FRAGMENT_TAG = "AccountFragment";
    private static final String SESSION_DIALOG_FRAGMENT_TAG = "SessionDialogFragment";
    private UserActivity context;
    private DatabaseController databaseController;
    private WelcomeFragment welcomeFragment;
    private CurrentSessionFragment currentSessionFragment;
    private SessionHistoryFragment sessionHistoryFragment;
    private AccountFragment accountFragment;
    private SessionDialogFragment sessionDialogFragment;
    private String username;
    private boolean displayingSessionHistory;

    public UserController(UserActivity context, Bundle savedInstanceState) {
        this.context = context;
        databaseController = DatabaseController.getInstance(this.context);
        databaseController.setDatabaseUIListener(this);
        initializeWelcomeFragment();
        initializeCurrentSessionFragment();
        initializeSessionHistoryFragment();
        initializeAccountFragment();
        initializeSessionDialogFragment();
        if (savedInstanceState != null){
            String lastFragmentTag = savedInstanceState.getString("lastFragmentTag");
            Fragment lastFragment = context.getSupportFragmentManager().findFragmentByTag(lastFragmentTag);
            this.context.setFragment(lastFragment, lastFragmentTag, false);
        } else {
            this.context.setFragment(welcomeFragment, WELCOME_FRAGMENT_TAG, false);
        }
    }

    public void setUsername(String username) {
        this.username = username;
        if (welcomeFragment != null){
            Log.d(TAG, "setUsername");
            welcomeFragment.setMessage(username);
        }
    }

    public String getUsername() {
        return username;
    }

    private void initializeWelcomeFragment() {
        welcomeFragment = (WelcomeFragment) context.getSupportFragmentManager().findFragmentByTag(WELCOME_FRAGMENT_TAG);
        if (welcomeFragment == null){
            welcomeFragment = new WelcomeFragment();
        }
        welcomeFragment.setController(this);
    }

    private void initializeCurrentSessionFragment() {
        currentSessionFragment = (CurrentSessionFragment) context.getSupportFragmentManager().findFragmentByTag(CURRENT_SESSION_FRAGMENT_TAG);
        if (currentSessionFragment == null){
            currentSessionFragment = new CurrentSessionFragment();
        }
        currentSessionFragment.setController(this);
    }

    private void initializeSessionHistoryFragment() {
        sessionHistoryFragment = (SessionHistoryFragment) context.getSupportFragmentManager().findFragmentByTag(SESSION_HISTORY_FRAGMENT_TAG);
        if (sessionHistoryFragment == null){
            sessionHistoryFragment = new SessionHistoryFragment();
        }
        sessionHistoryFragment.setController(this);
    }

    private void initializeAccountFragment() {
        accountFragment = (AccountFragment) context.getSupportFragmentManager().findFragmentByTag(ACCOUNT_FRAGMENT_TAG);
        if (accountFragment == null) {
            accountFragment = new AccountFragment();
        }
        accountFragment.setController(this);
    }

    private void initializeSessionDialogFragment() {
        sessionDialogFragment = (SessionDialogFragment) context.getSupportFragmentManager().findFragmentByTag(SESSION_DIALOG_FRAGMENT_TAG);
        if (sessionDialogFragment == null){
            sessionDialogFragment = new SessionDialogFragment();
        }
        sessionDialogFragment.setController(this);
    }


    public void displaySessionFragment() {
        displayingSessionHistory = false;
        context.setFragment(currentSessionFragment, CURRENT_SESSION_FRAGMENT_TAG, true);
    }

    public void displayHistoryFragment() {
        displayingSessionHistory = true;
        searchForSessions(-1, -1);
        context.setFragment(sessionHistoryFragment, SESSION_HISTORY_FRAGMENT_TAG, true);
    }

    public void displayAccountFragment() {
        context.setFragment(accountFragment, ACCOUNT_FRAGMENT_TAG, true);
    }

    public void displayWelcomeFragment() {
        context.setFragment(welcomeFragment, WELCOME_FRAGMENT_TAG, true);
    }

    public void displaySessionDialogFragment(boolean isServiceActive, long sessionID) {
        sessionDialogFragment = SessionDialogFragment.newInstance(isServiceActive, sessionID);
        sessionDialogFragment.setController(this);
        sessionDialogFragment.show(context.getSupportFragmentManager(), SESSION_DIALOG_FRAGMENT_TAG);
    }

    public void deleteAllSessions() {
        databaseController.deleteAllSession(username);
    }

    public void searchForSessions(long fromDate, long toDate) {
        databaseController.getSessionsBetween(username, fromDate, toDate);
    }

    public void getSessionInformation(long sessionID) {
        databaseController.getSessionInformation(sessionID);
    }

    @Override
    public void setSessionList(List<StepSession> list) {
        if (displayingSessionHistory) {
            final SessionHistoryAdapter adapter = new SessionHistoryAdapter(context, list);
            if (sessionHistoryFragment != null) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sessionHistoryFragment.setAdapter(adapter);
                    }
                });
            }
        } else {
            if (list.size() > 0) {
                if (currentSessionFragment != null) {
                    currentSessionFragment.setSessionID(list.get(list.size() - 1).getSessionID());
                }
            }
        }
    }

    @Override
    public void setSessionInformation(final StepSession stepSession) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sessionDialogFragment.setSession(stepSession);
            }
        });
    }
}
