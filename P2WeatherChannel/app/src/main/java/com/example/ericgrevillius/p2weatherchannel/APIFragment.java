package com.example.ericgrevillius.p2weatherchannel;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class APIFragment extends Fragment {
    private int weatherImageResource;
    private ImageView ivWeather;
    private TextView tvPressure;
    private String pressure;
    private TextView tvTemperature;
    private String temperature;
    private TextView tvHumidity;
    private String humidity;
    private TextView tvAltitude;
    private String altitude;
    private TextView tvTimeStamp;
    private String timeStamp;
    private TextView tvFetchMethod;
    private String fetchMethod;
    private Button btnVolley;
    private Button btnAsync;
    private Controller controller;

    public APIFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_api, container, false);
        initializeComponents(view, savedInstanceState);
        return view;
    }

    private void initializeComponents(View view, Bundle savedInstanceState) {
        ivWeather = view.findViewById(R.id.api_weather_image_view);
        tvPressure = view.findViewById(R.id.api_pressure_text_view);
        tvTemperature = view.findViewById(R.id.api_temperature_text_view);
        tvHumidity = view.findViewById(R.id.api_humidity_text_view);
        tvAltitude = view.findViewById(R.id.api_altitude_text_view);
        tvTimeStamp = view.findViewById(R.id.api_timestamp_text_view);
        tvFetchMethod = view.findViewById(R.id.api_fetch_method_text_view);
        ButtonListener buttonListener = new ButtonListener();
        btnVolley = view.findViewById(R.id.api_volley_button);
        btnVolley.setOnClickListener(buttonListener);
        btnAsync = view.findViewById(R.id.api_async_task_button);
        btnAsync.setOnClickListener(buttonListener);
        if (savedInstanceState != null){
            weatherImageResource = savedInstanceState.getInt("weatherImageResource");
            ivWeather.setImageResource(weatherImageResource);
            pressure = savedInstanceState.getString("pressure");
            tvPressure.setText(pressure);
            temperature = savedInstanceState.getString("temperature");
            tvTemperature.setText(temperature);
            humidity = savedInstanceState.getString("humidity");
            tvHumidity.setText(humidity);
            altitude = savedInstanceState.getString("altitude");
            tvAltitude.setText(altitude);
            timeStamp = savedInstanceState.getString("timeStamp");
            tvTimeStamp.setText(timeStamp);
            fetchMethod = savedInstanceState.getString("fetchMethod");
            tvFetchMethod.setText(fetchMethod);
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setValues(int weatherImageResource, String pressure, String temperature, String humidity, String altitude, String time, String fetch){
        this.weatherImageResource = weatherImageResource;
        if (ivWeather != null){
            ivWeather.setImageResource(weatherImageResource);
        }
        this.pressure = pressure;
        if (tvPressure != null){
            tvPressure.setText(this.pressure);
        }
        this.temperature = temperature;
        if (tvTemperature != null){
            tvTemperature.setText(this.temperature);
        }
        this.humidity = humidity;
        if (tvHumidity != null){
            tvHumidity.setText(this.humidity);
        }
        this.altitude = altitude;
        if (tvAltitude != null){
            tvAltitude.setText(this.altitude);
        }
        this.timeStamp = time;
        if (tvTimeStamp != null){
            tvTimeStamp.setText(timeStamp);
        }
        this.fetchMethod = fetch;
        if (tvFetchMethod != null){
            tvFetchMethod.setText(fetchMethod);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("weatherImageResource", weatherImageResource);
        outState.putString("pressure",pressure);
        outState.putString("temperature",temperature);
        outState.putString("humidity",humidity);
        outState.putString("altitude",altitude);
        outState.putString("timeStamp", timeStamp);
        outState.putString("fetchMethod", fetchMethod);
        super.onSaveInstanceState(outState);
    }

    private class ButtonListener implements View.OnClickListener{
        public void onClick(View view) {
            if (view.getId()==btnAsync.getId()){
                controller.requestOpenWeatherData("async");
            } else if (view.getId() == btnVolley.getId()){
                controller.requestOpenWeatherData("volley");
            }
        }
    }
}
