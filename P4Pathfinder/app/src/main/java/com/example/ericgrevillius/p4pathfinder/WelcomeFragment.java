package com.example.ericgrevillius.p4pathfinder;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class WelcomeFragment extends Fragment {
    private final static String MESSAGE_TAG = "message";
    private TextView welcomeTextView;
    private String message;

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
        if (savedInstanceState != null){
            message = savedInstanceState.getString(MESSAGE_TAG);
        }
        welcomeTextView.setText(message);
    }

    public void setMessage(String username){
        if(welcomeTextView != null){
            message = "Welcome " + username + "!";
            welcomeTextView.setText(message);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(MESSAGE_TAG, message);
        super.onSaveInstanceState(outState);
    }
}
