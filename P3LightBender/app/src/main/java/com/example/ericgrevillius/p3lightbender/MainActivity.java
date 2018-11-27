package com.example.ericgrevillius.p3lightbender;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final int WRITE_SETTINGS = 1;
    private SensorManager sensorManager;
    private SensorListener sensorListener;
    private Sensor lightSensor;
    private boolean isLightSensorPresent;
    private Sensor proximitySensor;
    private boolean isProximitySensorPresent;
    private CameraManager cameraManager;
    private String cameraID;
    private CameraCharacteristics cameraCharacteristics;
    private boolean isFlashLightEnabled;
    private SeekBar sbBrigthness;
    private SeekBarChangeListener seekBarListener;
    private RadioButton rbSystemBrightness;
    private RadioButton rbWindowBrightness;
    private TextView tvBrightness;
    private float brightnessLevel;
    private ContentResolver contentResolver;
    private Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeSensors();
        initializeCamera();
        initializeComponents();
        initializeScreenBrightness();
    }

    private void initializeSensors() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorListener = new SensorListener();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null){
                proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
                isProximitySensorPresent = true;
            }
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null){
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            isLightSensorPresent = true;
        } else {
            tvBrightness.setText(R.string.no_light_sensor_text);
        }
    }

    private void initializeCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
            try {
                if (cameraManager != null){
                    cameraID = cameraManager.getCameraIdList()[0];
                    cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraID);
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void initializeComponents(){
        sbBrigthness = findViewById(R.id.brightness_seek_bar);
        if (isLightSensorPresent){
            seekBarListener = new SeekBarChangeListener();
            sbBrigthness.setOnSeekBarChangeListener(seekBarListener);
        }
        rbSystemBrightness = findViewById(R.id.system_brightness_radio_button);
        rbWindowBrightness = findViewById(R.id.window_brightness_radio_button);
        tvBrightness = findViewById(R.id.brightness_text_view);
    }

    private void initializeScreenBrightness() {
        contentResolver = getContentResolver();
        window = getWindow();
        try {
            int lightLevel = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS);
            sbBrigthness.setProgress(lightLevel);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void enableFlashLight(boolean enabled){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)) {
                try {
                    cameraManager.setTorchMode(cameraID, enabled);
                    isFlashLightEnabled = enabled;
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void changeLightLevel(float brightness) {
        this.brightnessLevel = brightness;
        if (rbSystemBrightness.isEnabled()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (!Settings.System.canWrite(this)){
                    Intent i = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    startActivity(i);
                } else {
                    Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, (int)(brightnessLevel*255 ));
                }
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(this, new String []{android.Manifest.permission.WRITE_SETTINGS}, WRITE_SETTINGS);
                } else {
                    Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, (int)(brightness*255));
                }
            }
        } else if (rbWindowBrightness.isEnabled()){
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.screenBrightness = brightnessLevel;
            window.setAttributes(layoutParams);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isProximitySensorPresent){
            sensorManager.registerListener(sensorListener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (isLightSensorPresent){
            sensorManager.registerListener(sensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        if (isLightSensorPresent || isProximitySensorPresent){
            sensorManager.unregisterListener(sensorListener);
        }
        super.onPause();
    }
    private class SensorListener implements SensorEventListener{
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == proximitySensor.getType()){
                float distanceFromPhone = sensorEvent.values[0];
                if (distanceFromPhone < proximitySensor.getMaximumRange()){
                    if (!isFlashLightEnabled){
                        enableFlashLight(true);
                    }
                } else {
                    if (isFlashLightEnabled){
                        enableFlashLight(false);
                    }
                }
            } else if (sensorEvent.sensor.getType() == lightSensor.getType()) {
                float lightLevel = sensorEvent.values[0];
                if (lightLevel > 0 && lightLevel < 100){
                    changeLightLevel(1/lightLevel);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }

    private class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
