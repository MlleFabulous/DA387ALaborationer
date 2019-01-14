package com.example.ericgrevillius.p4pathfinder;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.ericgrevillius.p4pathfinder.database.Database;
import com.example.ericgrevillius.p4pathfinder.database.Step;
import com.example.ericgrevillius.p4pathfinder.database.StepSession;
import com.example.ericgrevillius.p4pathfinder.database.User;

import java.util.Calendar;

public class StepService extends Service implements SensorEventListener, DatabaseController.DatabaseServiceListener {
    private static final String TAG = "StepService";
    private LocalBinder binder;
    private DatabaseController databaseController;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private boolean isAccelerometerPresent;
    private Sensor stepDetectorSensor;
    private boolean isStepDetectorPresent;
    private User user;
    private StepSession session;
    private Step step;
    private double movement;
    private String username;
    private long lastTime;
    private int steps = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        binder = new LocalBinder();
        databaseController = DatabaseController.getInstance(getApplicationContext());
        databaseController.setDatabaseServiceListener(this);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isAccelerometerPresent = true;
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null){
            Log.d(TAG, "StepDetector is active");
            stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            isStepDetectorPresent = true;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        username = intent.getStringExtra("username");
        databaseController.getUserForService(username);
        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean stopService(Intent name) {
        if (isStepDetectorPresent) sensorManager.unregisterListener(this, stepDetectorSensor);
        if (isAccelerometerPresent) sensorManager.unregisterListener(this, accelerometerSensor);
        return super.stopService(name);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // If StepService needs to be unbound.
        return super.onUnbind(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == accelerometerSensor.getType()){
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTime > 250){
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];
                movement = Math.sqrt(x*x + y*y + z*z);
                lastTime = currentTime;
            }
        } else if (sensorEvent.sensor == stepDetectorSensor){
            long date = Calendar.getInstance().getTimeInMillis();
            String movementType = "walking";
            if (movement > 15f){
                movementType = "running";
            }
            Step step = new Step(session.getSessionID(), movementType, date);
            databaseController.insertStep(step);
//            Log.d(TAG, "Amount of steps: " + ++steps);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    @Override
    public void setUser(User user) {
        this.user = user;
        databaseController.insertStepSession(user.getUserID());
    }

    @Override
    public void setSession(StepSession stepSession) {
        this.session = stepSession;
        if (isAccelerometerPresent){
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (isStepDetectorPresent){
            sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public class LocalBinder extends Binder {
        StepService getService() {
            return StepService.this;
        }
    }
}
