package com.example.ericgrevillius.p4pathfinder;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.ericgrevillius.p4pathfinder.database.Database;

public class StepService extends Service implements SensorEventListener {
    private LocalBinder binder;
    private StepChangeListener stepChangeListener;
    private ThreadPool threadPool;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private boolean isAccelerometerPresent;
    private Sensor stepDetectorSensor;
    private boolean isStepDetectorPresent;

    @Override
    public void onCreate() {
        super.onCreate();
        //TODO: find the right sensor, most preferably the accelerometer.
        binder = new LocalBinder();
        threadPool = new ThreadPool(2);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isAccelerometerPresent = true;
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null){
            stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            isStepDetectorPresent = true;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (isAccelerometerPresent){
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (isStepDetectorPresent){
            sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // If StepService needs to be unbound.
        return super.onUnbind(intent);
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
