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

import com.google.android.gms.maps.model.LatLng;


/**
 * A simple {@link Fragment} subclass.
 */
public class DifferenceFragment extends Fragment implements SensorEventListener, LocationListener {
    private static final String TAG = "DifferenceFragment";
    private LocationManager locationManager;
    private SensorManager sensorManager;
    private Sensor pressureSensor;
    private boolean isPressureSensorPresent;
    private Sensor temperatureSensor;
    private boolean isTemperatureSensorPresent;
    private Sensor humiditySensor;
    private boolean isHumiditySensorPresent;
    private TextView tvCoordinates;
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
    private float sensorPressure;
    private float apiPressure;
    private float sensorTemperature;
    private float apiTemperature;
    private float sensorHumidity;
    private float apiHumidity;
    private float sensorAltitude;
    private float apiAltitude;

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
        tvCoordinates = view.findViewById(R.id.difference_coordinates_text_view);
        tvSensorPressure = view.findViewById(R.id.difference_pressure_sensor_text_view);
        tvAPIPressure = view.findViewById(R.id.difference_pressure_api_text_view);
        tvDifferencePressure = view.findViewById(R.id.difference_pressure_diff_text_view);
        tvSensorTemperature = view.findViewById(R.id.difference_temperature_sensor_text_view);
        tvAPITemperature = view.findViewById(R.id.difference_temperature_api_text_view);
        tvDifferenceTemperature = view.findViewById(R.id.difference_temperature_diff_text_view);
        tvSensorHumidity = view.findViewById(R.id.difference_humidity_sensor_text_view);
        tvAPIHumidity = view.findViewById(R.id.difference_humidity_api_text_view);
        tvDifferenceHumidity = view.findViewById(R.id.difference_humidity_diff_text_view);
        tvSensorAltitude = view.findViewById(R.id.difference_altitude_sensor_text_view);
        tvAPIAltitude = view.findViewById(R.id.difference_altitude_api_text_view);
        tvDifferenceAltitude = view.findViewById(R.id.difference_altitude_diff_text_view);
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
        } else { //sensor not found
            tvSensorPressure.setText(R.string.pressure_sensor_not_available_text);
            tvSensorAltitude.setText(R.string.cant_calculate_altitude_without_pressure_text);
            isPressureSensorPresent = false;
        }
        //initializing temperature sensor
        if (sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null){
            temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            isTemperatureSensorPresent = true;
        } else { //sensor not found
            tvSensorTemperature.setText(R.string.temperature_sensor_not_available_text);
            isTemperatureSensorPresent = false;
        }
        //initializing humidity sensor
        if (sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY ) != null){
            humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
            isHumiditySensorPresent = true;
        } else { //sensor not found
            tvSensorHumidity.setText(R.string.humidity_sensor_not_available_text);
            isHumiditySensorPresent = false;
        }

    }

    public void setValues(String coordinates, String pressure, String temperature, String humidity){
        String text;
        if (tvCoordinates != null){
            tvCoordinates.setText(coordinates);
        }
        apiPressure = Float.parseFloat(pressure);
        if (tvAPIPressure != null){
            text = "API: " + pressure + " hPa";
            tvAPIPressure.setText(text);
            if (!tvSensorPressure.getText().toString().matches(getString(R.string.pressure_sensor_not_available_text))){
                float differencePressure = apiPressure - sensorPressure;
                text = "Difference: (" + differencePressure + ")";
                tvDifferencePressure.setText(text);
            } else {
                tvDifferencePressure.setText(R.string.cant_calculate_text);
            }
        }
        apiTemperature = Float.parseFloat(temperature);
        if (tvAPITemperature != null){
            text = "API: " + temperature + " °C";
            tvAPITemperature.setText(text);
            if (!tvSensorTemperature.getText().toString().matches(getString(R.string.temperature_sensor_not_available_text))){
                float differenceTemperature = apiTemperature - sensorTemperature;
                text = "Difference: (" + differenceTemperature + ")";
                tvDifferenceTemperature.setText(text);
            } else {
                tvDifferenceTemperature.setText(R.string.cant_calculate_text);
            }
        }
        apiHumidity = Float.parseFloat(humidity);
        if (tvAPIHumidity != null){
            text = "API: " + humidity + " %";
            tvAPIHumidity.setText(text);
            if (!tvSensorHumidity.getText().toString().matches(getString(R.string.humidity_sensor_not_available_text))){
                float differenceHumidity = apiHumidity - sensorHumidity;
                text = "Difference: (" + differenceHumidity + ")";
                tvDifferenceHumidity.setText(text);
            } else {
                tvDifferenceHumidity.setText(R.string.cant_calculate_text);
            }
        }
        apiAltitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, Float.parseFloat(pressure));
        if (tvAPIAltitude != null){
            text = "API: " + apiAltitude + "m above sea level";
            tvAPIAltitude.setText(text);
            if (!tvSensorAltitude.getText().toString().matches(getString(R.string.cant_calculate_altitude_without_pressure_text))){
                float differenceAltitude = apiAltitude - sensorAltitude;
                text = "Difference: (" + differenceAltitude + ")";
                tvDifferenceAltitude.setText(text);
            } else {
                tvDifferenceAltitude.setText(R.string.cant_calculate_text);
            }
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
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,60000,0,this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,60000,0,this);
        }
    }

    @Override
    public void onPause() {
        locationManager.removeUpdates(this);
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        String text;
        if (sensorEvent.sensor.getType() == pressureSensor.getType()){
            sensorPressure = sensorEvent.values[0];
            text = "Sensor: " + sensorPressure + " hPa";
            tvSensorPressure.setText(text);
            float differencePressure = apiPressure - sensorPressure;
            text = "Difference: (" + differencePressure + ")";
            tvDifferencePressure.setText(text);
            sensorAltitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, sensorPressure);
            text = "Sensor: " + sensorAltitude + "m above sea level";
            tvSensorAltitude.setText(text);
            float differenceAltitude = apiAltitude - sensorAltitude;
            text = "Difference: (" + differenceAltitude + ")";
            tvDifferenceAltitude.setText(text);
        } else if (sensorEvent.sensor.getType() == temperatureSensor.getType()){
            sensorTemperature = sensorEvent.values[0];
            text = "Sensor: " + sensorTemperature + " °C";
            tvSensorTemperature.setText(text);
            float differenceTemperature = apiTemperature - sensorTemperature;
            text = "Difference: (" + differenceTemperature + ")";
            tvDifferenceTemperature.setText(text);
        } else if (sensorEvent.sensor.getType() == humiditySensor.getType()){
            sensorHumidity = sensorEvent.values[0];
            text = "Sensor: " + sensorHumidity + " %";
            tvSensorHumidity.setText(text);
            float differenceHumidity = apiHumidity - sensorHumidity;
            text = "Difference: (" + differenceHumidity + ")";
            tvDifferenceHumidity.setText(text);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        controller.setLocation(new LatLng(location.getLatitude(),location.getLongitude()), TAG);
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
