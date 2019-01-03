package com.example.ericgrevillius.p4pathfinder;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class WelcomeFragment extends Fragment {
    private static final String TAG = "WelcomeFragment";
    private final static String MESSAGE_TAG = "message";
    private TextView welcomeTextView;
    private String message;
    private Button newSessionButton;
    private Button sessionHistoryButton;
    private UserController controller;

    public WelcomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome,container, false);
        initializeComponents(view, savedInstanceState);
        return view;
    }

    private void initializeComponents(View view, Bundle savedInstanceState) {
        welcomeTextView = view.findViewById(R.id.welcome_text_view);
        ButtonListener buttonListener = new ButtonListener();
        newSessionButton = view.findViewById(R.id.welcome_new_session_button);
        newSessionButton.setOnClickListener(buttonListener);
        if (savedInstanceState != null){
            message = savedInstanceState.getString(MESSAGE_TAG);
        }
        welcomeTextView.setText(message);
    }

    public void setMessage(String username){
        message = "Welcome " + username + "!";
        if(welcomeTextView != null){
            Log.d(TAG, "setMessage");
            welcomeTextView.setText(message);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(MESSAGE_TAG, message);
        super.onSaveInstanceState(outState);
    }

    public void setController(UserController controller) {
        this.controller = controller;
    }

    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int viewID = view.getId();
            if (viewID == newSessionButton.getId()){
                //TODO: start a new session;
                controller.displaySessionFragment();
            }
            if (viewID == sessionHistoryButton.getId()){
                controller.displayHistoryFragment();
            }
        }
    }
}
