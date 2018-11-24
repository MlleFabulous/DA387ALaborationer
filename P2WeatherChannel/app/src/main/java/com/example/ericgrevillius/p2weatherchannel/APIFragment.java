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
    //TODO: add TextView displaying coordinates.
    private static final String TAG = "APIFragment";
    private TextView tvCoordinates;
    private String coordinates;
    private TextView tvWeather;
    private String weather;
    private ImageView ivWeather;
    private int weatherImageResource;
    private TextView tvPressure;
    private String pressure;
    private TextView tvTemperature;
    private String temperature;
    private TextView tvHumidity;
    private String humidity;
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
        tvCoordinates = view.findViewById(R.id.api_coordinates_text_view);
        tvWeather = view.findViewById(R.id.api_weather_text_view);
        ivWeather = view.findViewById(R.id.api_weather_image_view);
        tvPressure = view.findViewById(R.id.api_pressure_text_view);
        tvTemperature = view.findViewById(R.id.api_temperature_text_view);
        tvHumidity = view.findViewById(R.id.api_humidity_text_view);
        tvTimeStamp = view.findViewById(R.id.api_timestamp_text_view);
        tvFetchMethod = view.findViewById(R.id.api_fetch_method_text_view);
        ButtonListener buttonListener = new ButtonListener();
        btnVolley = view.findViewById(R.id.api_volley_button);
        btnVolley.setOnClickListener(buttonListener);
        btnAsync = view.findViewById(R.id.api_async_task_button);
        btnAsync.setOnClickListener(buttonListener);
        if (savedInstanceState != null){
            coordinates = savedInstanceState.getString(coordinates);
            tvCoordinates.setText(coordinates);
            weather = savedInstanceState.getString("weather");
            tvWeather.setText(weather);
            weatherImageResource = savedInstanceState.getInt("weatherImageResource");
            ivWeather.setImageResource(weatherImageResource);
            pressure = savedInstanceState.getString("pressure");
            tvPressure.setText(pressure);
            temperature = savedInstanceState.getString("temperature");
            tvTemperature.setText(temperature);
            humidity = savedInstanceState.getString("humidity");
            tvHumidity.setText(humidity);
            timeStamp = savedInstanceState.getString("timeStamp");
            tvTimeStamp.setText(timeStamp);
            fetchMethod = savedInstanceState.getString("fetchMethod");
            tvFetchMethod.setText(fetchMethod);
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setValues(String coordinates, String weather, int weatherImageResource, String pressure, String temperature, String humidity, String time, String fetch){
        this.coordinates = coordinates;
        if (tvCoordinates != null){
            tvCoordinates.setText(this.coordinates);
        }
        this.weather = weather;
        if (tvWeather != null){
            tvWeather.setText(this.weather);
        }
        this.weatherImageResource = weatherImageResource;
        if (ivWeather != null){
            ivWeather.setImageResource(weatherImageResource);
        }
        this.pressure = pressure + " hPa";
        if (tvPressure != null){
            tvPressure.setText(this.pressure );
        }
//        int temp = Integer.parseInt(temperature);
        this.temperature = temperature + " °C";
        if (tvTemperature != null){
            tvTemperature.setText(this.temperature);
        }
        this.humidity = humidity + " %";
        if (tvHumidity != null){
            tvHumidity.setText(this.humidity);
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
        outState.putString("coordinates", coordinates);
        outState.putString("weather", weather);
        outState.putInt("weatherImageResource", weatherImageResource);
        outState.putString("pressure",pressure);
        outState.putString("temperature",temperature);
        outState.putString("humidity",humidity);
        outState.putString("timeStamp", timeStamp);
        outState.putString("fetchMethod", fetchMethod);
        super.onSaveInstanceState(outState);
    }

    private class ButtonListener implements View.OnClickListener{
        public void onClick(View view) {
            if (view.getId()==btnAsync.getId()){
                controller.requestOpenWeatherData("async",TAG);
            } else if (view.getId() == btnVolley.getId()){
                controller.requestOpenWeatherData("volley",TAG);
            }
        }
    }
}
