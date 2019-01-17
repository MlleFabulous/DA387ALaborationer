package com.example.ericgrevillius.p4pathfinder;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


public class CurrentSessionFragment extends Fragment implements SensorEventListener {
    private static final String TAG = "CurrentSessionFragment";
    private UserController controller;
    private ImageView compassImageView;
    private Button startStopSessionButton;
    private Button sessionInformationButton;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean isAccelerometerPresent;
    private Sensor magnetometer;
    private boolean isMagnetometerPresent;
    private long lastTime = 0;
    private float currentDegree;
    private float lastDegree;
    private float [] rotationMatrix;
    private float [] orientation;
    private float [] lastAcceleromter;
    private float [] lastMagnetometer;
    private boolean isLastAccelerometerSet;
    private boolean isLastMagnetometerSet;
    private Intent stepsIntent;
    StepService service;
    boolean isServiceBound;
    private long sessionID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_session, container, false);
        initializeComponents(view, savedInstanceState);
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isAccelerometerPresent = true;
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null){
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            isMagnetometerPresent = true;
        }
        return view;
    }

    private void initializeComponents(View view, Bundle savedInstanceState) {
        compassImageView = view.findViewById(R.id.current_compass_image_view);
        ButtonListener buttonListener = new ButtonListener();
        startStopSessionButton = view.findViewById(R.id.current_session_start_stop_button);
        startStopSessionButton.setOnClickListener(buttonListener);
        sessionInformationButton = view.findViewById(R.id.current_session_information_button);
        sessionInformationButton.setOnClickListener(buttonListener);
        if (savedInstanceState != null){
            isServiceBound = savedInstanceState.getBoolean("isServiceBound");
        }
        if (isServiceBound){
            startStopSessionButton.setText(R.string.stop_session_text);
            sessionInformationButton.setEnabled(true);
        }

    }

    public void setController(UserController controller) {
        this.controller = controller;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isAccelerometerPresent) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (isMagnetometerPresent){
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        if (isAccelerometerPresent) {
            sensorManager.unregisterListener(this, accelerometer);
        }
        if (isMagnetometerPresent){
            sensorManager.unregisterListener(this, magnetometer);
        }
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("isServiceBound", isServiceBound);
        super.onSaveInstanceState(outState);
    }

    private void animateImageView(float angleInDegrees) {
        RotateAnimation rotateAnimation = new RotateAnimation(currentDegree, - angleInDegrees,
                Animation.RELATIVE_TO_SELF,0.5f,
                Animation.RELATIVE_TO_SELF,0.5f);
        rotateAnimation.setDuration(250);
        rotateAnimation.setFillAfter(true);
        compassImageView.startAnimation(rotateAnimation);
        lastDegree = currentDegree;
        currentDegree =  - angleInDegrees;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        long currentTime = System.currentTimeMillis();
        if (sensorEvent.sensor == accelerometer) {
            lastAcceleromter = sensorEvent.values;
            isLastAccelerometerSet = true;
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            double total = Math.sqrt(x*x + y*y + z*z);
            if (total > 20){
//                Log.d(TAG, "onSensorChanged: ");
                animateImageView( currentDegree + 360f);
                animateImageView(currentDegree - 360f);
            }
        }
        if (sensorEvent.sensor == magnetometer){
            lastMagnetometer = sensorEvent.values;
            isLastMagnetometerSet = true;
        }
        if (isLastAccelerometerSet && isLastMagnetometerSet && currentTime - lastTime >= 250) {
            rotationMatrix = new float[9];
            SensorManager.getRotationMatrix(rotationMatrix, null, lastAcceleromter, lastMagnetometer);
            orientation = new float[3];
            SensorManager.getOrientation(rotationMatrix, orientation);
            float azimuthInRadians = orientation[0];
            float azimuthInDegrees = (float) ((Math.toDegrees(azimuthInRadians) + 360) % 360);
            animateImageView(azimuthInDegrees);
            lastTime = currentTime;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void setSessionID(long sessionID) {
        this.sessionID = sessionID;
        if (this.isVisible()) {
            controller.displaySessionDialogFragment(isServiceBound, this.sessionID);
        }
    }

    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == startStopSessionButton.getId()) {
                if (getActivity() != null){
                    if (isServiceBound){ //stop service
                        stepsIntent = new Intent(getActivity(), StepService.class);
                        getActivity().stopService(stepsIntent);
                        isServiceBound = false;
                        startStopSessionButton.setText(getString(R.string.start_session_text));
                        controller.searchForSessions(-1,-1);
                    } else { //start service
                        stepsIntent = new Intent(getActivity(),StepService.class);
                        stepsIntent.putExtra("username", controller.getUsername());
                        isServiceBound = true;
                        getActivity().startService(stepsIntent);
                        startStopSessionButton.setText(getString(R.string.stop_session_text));
                    }
                    sessionInformationButton.setEnabled(isServiceBound);
                }
            } else if (id == sessionInformationButton.getId()) {
                controller.searchForSessions(-1,-1);
            }
        }
    }
}
