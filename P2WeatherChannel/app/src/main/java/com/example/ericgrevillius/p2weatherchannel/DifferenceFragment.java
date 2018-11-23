package com.example.ericgrevillius.p2weatherchannel;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class DifferenceFragment extends Fragment implements SensorEventListener, LocationListener {
    private LocationManager locationManager;
    private SensorManager sensorManager;
    private Sensor pressureSensor;
    private boolean isPressureSensorPresent;
    private Sensor temperatureSensor;
    private boolean isTemperatureSensorPresent;
    private Sensor humiditySensor;
    private boolean isHumiditySensorPresent;
    private TextView tvSensorPressure;
    private TextView tvAPIPressure;
    private TextView tvDifferencePressure;
    private TextView tvSensorTemperature;
    private TextView tvAPITemperature;
    private TextView tvDifferenceTemperature;
    private TextView tvSensorHumidity;
    private TextView tvAPIHumidity;
    private TextView tvDifferenceHumidity;
    private TextView tvSensorAltitude;
    private TextView tvAPIAltitude;
    private TextView tvDifferenceAltitude;
    private Controller controller;

    public DifferenceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_difference, container, false);
        initializeComponents(view);
        initializeLocation();
        initializeSensors();
        return view;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    private void initializeComponents(View view){
        //TODO: init components
    }

    private void initializeLocation() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    private void initializeSensors() {
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        //initializing pressure sensor
        if (sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE ) != null){
            pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            isPressureSensorPresent = true;
        } else {
            tvSensorAltitude.setText(R.string.cant_calculate_altitude_without_pressure_text);
            tvSensorPressure.setText(R.string.pressure_sensor_not_available_text);
            isPressureSensorPresent = false;
        }
        //initializing temperature sensor
        if (sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null){
            temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            isTemperatureSensorPresent = true;
        } else {
            tvSensorTemperature.setText(R.string.temperature_sensor_not_available_text);
            isTemperatureSensorPresent = false;
        }
        //initializing humidity sensor
        if (sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY ) != null){
            humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
            isHumiditySensorPresent = true;
        } else {
            tvSensorHumidity.setText(R.string.humidity_sensor_not_available_text);
            isHumiditySensorPresent = false;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (isPressureSensorPresent){
            sensorManager.registerListener(this,pressureSensor,SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (isTemperatureSensorPresent){
            sensorManager.registerListener(this,temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (isHumiditySensorPresent){
            sensorManager.registerListener(this, humiditySensor,SensorManager.SENSOR_DELAY_NORMAL);
        }
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,0,this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,this);
        }
    }

    @Override
    public void onPause() {
//        locationManager.removeUpdates(this);
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
