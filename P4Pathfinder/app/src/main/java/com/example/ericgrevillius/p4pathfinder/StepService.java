package com.example.ericgrevillius.p4pathfinder;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class StepService extends Service implements SensorEventListener {
    private LocalBinder binder;
    private StepChangeListener stepChangeListener;
    @Override
    public void onCreate() {
        super.onCreate();
        //TODO: find the right sensor, most preferably the accelerometer.
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //TODO: handle sensor values
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void setStepChangeListener(StepChangeListener stepChangeListener){
        this.stepChangeListener = stepChangeListener;
    }

    public class LocalBinder extends Binder {
        StepService getService() {
            return StepService.this;
        }
    }
}
