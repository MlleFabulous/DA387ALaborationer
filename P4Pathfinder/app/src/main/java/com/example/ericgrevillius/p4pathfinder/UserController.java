package com.example.ericgrevillius.p4pathfinder;

import android.media.MediaCas;
import android.util.Log;

public class UserController {
    private static final String TAG = "UserController";
    private static final String WELCOME_FRAGMENT_TAG = "WelcomeFragment";
    private static final String CURRENT_SESSION_FRAGMENT_TAG = "CurrentSessionFragment";
    private static final String SESSION_HISTORY_FRAGMENT_TAG = "SessionHistoryFragment";
    private static final String ACCOUNT_FRAGMENT_TAG = "AccountFragment";
    private UserActivity context;
    private DatabaseController databaseController;
    private WelcomeFragment welcomeFragment;
    private CurrentSessionFragment currentSessionFragment;
    private SessionHistoryFragment sessionHistoryFragment;
    private AccountFragment accountFragment;
    private String username;

    public UserController(UserActivity context) {
        this.context = context;
        databaseController = new DatabaseController(context);
        initializeWelcomeFragment();
        initializeCurrentSessionFragment();
        initializeSessionHistoryFragment();
        initializeAccountFragment();
    }

    public void setUsername(String username) {
        this.username = username;
        if (welcomeFragment != null){
            Log.d(TAG, "setUsername");
            welcomeFragment.setMessage(username);
        }
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


    public void displaySessionFragment() {
        context.setFragment(currentSessionFragment, CURRENT_SESSION_FRAGMENT_TAG, true);
        Log.d(TAG, "displaySessionFragment");
    }

    public void displayHistoryFragment() {
        context.setFragment(sessionHistoryFragment, SESSION_HISTORY_FRAGMENT_TAG, true);
        Log.d(TAG, "displayHistoryFragment");
    }

    public void displayAccountFragment() {
        context.setFragment(accountFragment, ACCOUNT_FRAGMENT_TAG, true);
        Log.d(TAG, "displayAccountFragment");
    }

    public void displayWelcomeFragment() {
        context.setFragment(welcomeFragment, WELCOME_FRAGMENT_TAG, true);
        Log.d(TAG, "displayWelcomeFragment");
    }
}
