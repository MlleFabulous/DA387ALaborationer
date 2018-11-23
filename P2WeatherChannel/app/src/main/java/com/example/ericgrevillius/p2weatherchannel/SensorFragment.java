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
public class SensorFragment extends Fragment implements SensorEventListener, LocationListener {
    private TextView tvPressure;
    private TextView tvTemperature;
    private TextView tvHumidity;
    private TextView tvAltitude;
    private Controller controller;
    private LocationManager locationManager;
    private SensorManager sensorManager;
    private Sensor pressureSensor;
    private boolean isPressureSensorPresent;
    private Sensor temperatureSensor;
    private boolean isTemperatureSensorPresent;
    private Sensor humiditySensor;
    private boolean isHumiditySensorPresent;

    public SensorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sensor, container, false);
        initializeComponents(view);
//        initializeLocation();
        initializeSensors();
        return view;
    }

    private void initializeComponents(View view) {
        tvPressure = view.findViewById(R.id.sensor_pressure_text_view);
        tvTemperature = view.findViewById(R.id.sensor_temperature_text_view);
        tvHumidity = view.findViewById(R.id.sensor_humidity_text_view);
        tvAltitude = view.findViewById(R.id.sensor_altitude_text_view);
    }

//    private void initializeLocation() {
//        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//    }

    private void initializeSensors() {
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        //initializing pressure sensor
        if (sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE ) != null){
            pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            isPressureSensorPresent = true;
        } else {
            tvAltitude.setText(R.string.cant_calculate_altitude_without_pressure_text);
            tvPressure.setText(R.string.pressure_sensor_not_available_text);
            isPressureSensorPresent = false;
        }
        //initializing temperature sensor
        if (sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null){
            temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            isTemperatureSensorPresent = true;
        } else {
            tvTemperature.setText(R.string.temperature_sensor_not_available_text);
            isTemperatureSensorPresent = false;
        }
        //initializing humidity sensor
        if (sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY ) != null){
            humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
            isHumiditySensorPresent = true;
        } else {
            tvHumidity.setText(R.string.humidity_sensor_not_available_text);
            isHumiditySensorPresent = false;
        }

    }

    public void setController(Controller controller) {
        this.controller = controller;
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
//        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,0,this);
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,this);
//        }
    }

    @Override
    public void onPause() {
//        locationManager.removeUpdates(this);
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == pressureSensor.getType()){
            float pressure = sensorEvent.values[0];
            tvPressure.setText(pressure + " hPa");
            float altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure);
            tvAltitude.setText(altitude + " m above sea level");
        } else if (sensorEvent.sensor.getType() == temperatureSensor.getType()){
            tvTemperature.setText(sensorEvent.values[0] + " °C");
        } else if (sensorEvent.sensor.getType() == humiditySensor.getType()){
            tvTemperature.setText(sensorEvent.values[0] + " %");
        }
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
