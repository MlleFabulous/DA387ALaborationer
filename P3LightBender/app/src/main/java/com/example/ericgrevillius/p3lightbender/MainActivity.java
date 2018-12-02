package com.example.ericgrevillius.p3lightbender;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.concurrent.ThreadPoolExecutor;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int WRITE_SETTINGS = 1;
    private static final long MORSE_UNIT = 500;
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
    private SeekBar sbBrightness;
    private RadioButton rbSystemBrightness;
    private RadioButton rbWindowBrightness;
    private TextView tvBrightness;
    private CheckBox cbAutoBrightness;
    private float brightnessLevel;
    private float lightLevel;
    private ContentResolver contentResolver;
    private Window window;
    private Button btnSOSMorse;
    private boolean isSendingSOS = false;
    private Button btnCustomMorse;
    private long lastSignal;
    private long currentSignal;
    Thread thread;

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
            if (sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) {
                proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
                isProximitySensorPresent = true;
            }
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
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
                if (cameraManager != null) {
                    cameraID = cameraManager.getCameraIdList()[0];
                    cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraID);
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }


    private void initializeComponents(){
        sbBrightness = findViewById(R.id.brightness_seek_bar);

        rbSystemBrightness = findViewById(R.id.system_brightness_radio_button);
        rbWindowBrightness = findViewById(R.id.window_brightness_radio_button);
        tvBrightness = findViewById(R.id.brightness_text_view);
        cbAutoBrightness = findViewById(R.id.auto_brightness_check_box);
        cbAutoBrightness.setOnCheckedChangeListener(new CheckBoxListener());
        ButtonListener buttonListener = new ButtonListener();
        btnSOSMorse = findViewById(R.id.sos_button);
        btnSOSMorse.setOnClickListener(buttonListener);
        btnCustomMorse = findViewById(R.id.custom_morse_button);
        btnCustomMorse.setOnClickListener(buttonListener);
        if (isLightSensorPresent) {
            SeekBarChangeListener seekBarListener = new SeekBarChangeListener();
            sbBrightness.setOnSeekBarChangeListener(seekBarListener);
            sbBrightness.setMax(100);
        }

    }

    private void initializeScreenBrightness() {
        contentResolver = getContentResolver();
        window = getWindow();
        try {
            int lightLevel = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS);
//            Log.d(TAG, "initializeScreenBrightness: Light level: " + lightLevel);
            sbBrightness.setProgress(lightLevel);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void enableFlashLight(boolean enabled) {
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
        if (rbSystemBrightness.isEnabled()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.System.canWrite(this)) {
                    Intent i = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    startActivity(i);
                } else {
                    Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, (int) (brightnessLevel * 255));
                }
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_SETTINGS}, WRITE_SETTINGS);
                } else {
                    Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, (int) (brightnessLevel * 255));
                }
            }
        } else if (rbWindowBrightness.isEnabled()) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.screenBrightness = brightnessLevel;
            window.setAttributes(layoutParams);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isProximitySensorPresent) {
            sensorManager.registerListener(sensorListener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (cbAutoBrightness.isChecked()) {
            if (isLightSensorPresent) {
                sensorManager.registerListener(sensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
    }

    @Override
    protected void onPause() {
        if (isFlashLightEnabled) {
            enableFlashLight(false);
        }
        if (cbAutoBrightness.isChecked()) {
            if (isLightSensorPresent) {
                sensorManager.unregisterListener(sensorListener, lightSensor);
            }
        }
        if (isProximitySensorPresent){
            sensorManager.unregisterListener(sensorListener,proximitySensor);
        }
        super.onPause();
    }

    private class SensorListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == proximitySensor.getType()) {
                if (isLightSensorPresent){
                    if (lightLevel < 20){
                        float distanceFromPhone = sensorEvent.values[0];
                        if (distanceFromPhone < proximitySensor.getMaximumRange()) {
                            if (!isFlashLightEnabled) {
                                enableFlashLight(true);
                            }
                        } else {
                            if (isFlashLightEnabled) {
                                enableFlashLight(false);
                            }
                        }
                    }
                }
            } else if (sensorEvent.sensor.getType() == lightSensor.getType()) {
                lightLevel = sensorEvent.values[0];
                if (lightLevel > 100) {
                    lightLevel = 100.0f;
                }
                if (lightLevel > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        sbBrightness.setProgress((int) (lightLevel), true);
                    } else {
                        sbBrightness.setProgress((int) (lightLevel));
                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }

    private class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (i > 0) {
                float lightLevel = i;
                changeLightLevel(lightLevel / 100);
                if (i < 20) {
                    tvBrightness.setText(R.string.very_dark_text);
                } else if (i < 40) {
                    tvBrightness.setText("Quite dark");
                } else if (i < 60) {
                    tvBrightness.setText("Normal");
                } else if (i < 80) {
                    tvBrightness.setText("Quite bright");
                } else {
                    tvBrightness.setText("Very bright");
                }
            }
        }

        @Override
        public void onStartTrackingTouch (SeekBar seekBar){
            cbAutoBrightness.setChecked(false);
        }

        @Override
        public void onStopTrackingTouch (SeekBar seekBar){

        }
    }

    private class CheckBoxListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if (isChecked) {
                if (isLightSensorPresent){
                    sensorManager.registerListener(sensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
                }
            } else {
                if (isLightSensorPresent){
                    sensorManager.unregisterListener(sensorListener, lightSensor);
                }
            }
        }
    }

    private class ButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if (view.getId() == btnSOSMorse.getId()){
                if (!isSendingSOS) {
                    isSendingSOS = true;
                    sendMorseMessage(parseMorseMessage("SOS"));
                    btnSOSMorse.setText(R.string.stop_s_o_s_text);
                } else {
                    isSendingSOS = false;
                    if(thread.isAlive()){
                        try {
                            thread.interrupt();
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    btnSOSMorse.setText(R.string.send_s_o_s_text);
                }
            } else if (view.getId() == btnCustomMorse.getId()){

            }
        }
    }

    private String parseMorseMessage(String textToParse){
        StringBuilder toReturn = new StringBuilder();
        for (int i = 0; i < textToParse.length(); i++){
            switch (textToParse.charAt(i)){
                case 'A':
                    toReturn.append(MorseCodeAlphabet.A );
                    break;
                case 'B':
                    toReturn.append(MorseCodeAlphabet.B );
                    break;
                case 'C':
                    toReturn.append(MorseCodeAlphabet.C );
                    break;
                case 'D':
                    toReturn.append(MorseCodeAlphabet.D );
                    break;
                case 'E':
                    toReturn.append(MorseCodeAlphabet.E );
                    break;
                case 'F':
                    toReturn.append(MorseCodeAlphabet.F );
                    break;
                case 'G':
                    toReturn.append(MorseCodeAlphabet.G );
                    break;
                case 'H':
                    toReturn.append(MorseCodeAlphabet.H );
                    break;
                case 'I':
                    toReturn.append(MorseCodeAlphabet.I );
                    break;
                case 'J':
                    toReturn.append(MorseCodeAlphabet.J );
                    break;
                case 'K':
                    toReturn.append(MorseCodeAlphabet.K );
                    break;
                case 'L':
                    toReturn.append(MorseCodeAlphabet.L );
                    break;
                case 'M':
                    toReturn.append(MorseCodeAlphabet.M );
                    break;
                case 'N':
                    toReturn.append(MorseCodeAlphabet.N );
                    break;
                case 'O':
                    toReturn.append(MorseCodeAlphabet.O );
                    break;
                case 'P':
                    toReturn.append(MorseCodeAlphabet.P );
                    break;
                case 'Q':
                    toReturn.append(MorseCodeAlphabet.Q );
                    break;
                case 'R':
                    toReturn.append(MorseCodeAlphabet.R );
                    break;
                case 'S':
                    toReturn.append(MorseCodeAlphabet.S );
                    break;
                case 'T':
                    toReturn.append(MorseCodeAlphabet.T );
                    break;
                case 'U':
                    toReturn.append(MorseCodeAlphabet.U );
                    break;
                case 'V':
                    toReturn.append(MorseCodeAlphabet.V );
                    break;
                case 'W':
                    toReturn.append(MorseCodeAlphabet.W );
                    break;
                case 'X':
                    toReturn.append(MorseCodeAlphabet.X );
                    break;
                case 'Y':
                    toReturn.append(MorseCodeAlphabet.Y );
                    break;
                case 'Z':
                    toReturn.append(MorseCodeAlphabet.Z );
                    break;
                case 'Å':
                    toReturn.append(MorseCodeAlphabet.Å );
                    break;
                case 'Ä':
                    toReturn.append(MorseCodeAlphabet.Ä );
                    break;
                case 'Ö':
                    toReturn.append(MorseCodeAlphabet.Ö );
                    break;
                case '0':
                    toReturn.append(MorseCodeAlphabet.m0 );
                    break;
                case '1':
                    toReturn.append(MorseCodeAlphabet.m1);
                    break;
                case '2':
                    toReturn.append(MorseCodeAlphabet.m2);
                    break;
                case '3':
                    toReturn.append(MorseCodeAlphabet.m3);
                    break;
                case '4':
                    toReturn.append(MorseCodeAlphabet.m4);
                    break;
                case '5':
                    toReturn.append(MorseCodeAlphabet.m5);
                    break;
                case '6':
                    toReturn.append(MorseCodeAlphabet.m6);
                    break;
                case '7':
                    toReturn.append(MorseCodeAlphabet.m7);
                    break;
                case '8':
                    toReturn.append(MorseCodeAlphabet.m8);
                    break;
                case '9':
                    toReturn.append(MorseCodeAlphabet.m9);
                    break;
                case ' ':
                    toReturn.append("       ");
                    break;
            }
            if(i < textToParse.length()-1){
                if (textToParse.charAt(i) != ' '){
                    toReturn.append("   ");
                }
            }
        }
        return toReturn.toString();
    }

    private void sendMorseMessage(final String morseMessage) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    do{
                        for (int i = 0; i < morseMessage.length(); i++){
                            char signal = morseMessage.charAt(i);
                            if (signal == '.'){
                                if (!isFlashLightEnabled){
                                    enableFlashLight(true);
                                }
                                Thread.sleep(MORSE_UNIT);
                            } else if (signal == '-'){
                                if (!isFlashLightEnabled){
                                    enableFlashLight(true);
                                }
                                Thread.sleep(MORSE_UNIT *3);
                            } else if(signal == ' '){
                                if (isFlashLightEnabled){
                                    enableFlashLight(false);
                                }
                                Thread.sleep(MORSE_UNIT);
                            }
                        }
                        if (isFlashLightEnabled){
                            enableFlashLight(false);
                        }
                        if (isSendingSOS){
                            Thread.sleep(MORSE_UNIT * 7);
                        }
                    } while(isSendingSOS);
                } catch (InterruptedException e) {
                    enableFlashLight(false);
                    Log.d(TAG, "Thread Interrupted");

                }
            }
        });
        thread.start();
        Log.d(TAG, "Thread Started");
    }
}

