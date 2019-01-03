package com.example.ericgrevillius.p4pathfinder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


public class CurrentSessionFragment extends Fragment {
    private static final String TAG = "CurrentSessionFragment";
    private UserController controller;
    private ImageView compassImageView;
    private Button startStopSessionButton;
    private Button sessionInformationButton;
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_session, container, false);
        initializeComponents(view, savedInstanceState);
        return view;
    }

    private void initializeComponents(View view, Bundle savedInstanceState) {
        compassImageView = view.findViewById(R.id.current_compass_image_view);
        ButtonListener buttonListener = new ButtonListener();
        startStopSessionButton = view.findViewById(R.id.current_session_start_stop_button);
        startStopSessionButton.setOnClickListener(buttonListener);
        sessionInformationButton = view.findViewById(R.id.current_session_information_button);
        sessionInformationButton.setOnClickListener(buttonListener);
    }

    public void setController(UserController controller) {
        this.controller = controller;
    }

    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == startStopSessionButton.getId()){
                //TODO: Start session
            }
            if (id == sessionInformationButton.getId()){
                //TODO; display a dialogfragment with session information
            }
        }
    }
}
