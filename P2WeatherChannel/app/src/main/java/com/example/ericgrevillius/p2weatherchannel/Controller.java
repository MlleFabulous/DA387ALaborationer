package com.example.ericgrevillius.p2weatherchannel;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;
import java.util.Scanner;

public class Controller {
    private static final String TAG = "Controller";
    private static final String API_FRAGMENT_TAG = "apiFragment";
    private static final String DIFFERENCE_FRAGMENT_TAG = "differenceFragment";
    private static final String MAPS_FRAGMENT_TAG = "mapsFragment";
    private static final String SENSOR_FRAGMENT_TAG = "sensorFragment";
    private APIFragment apiFragment;
    private DifferenceFragment differenceFragment;
    private MapsFragment mapsFragment;
    private SensorFragment sensorFragment;
    private MainActivity context;
    private OpenWeatherAPI openWeatherAPI;
    private LatLng location;

    public Controller(MainActivity activity){
        context = activity;
        openWeatherAPI = new OpenWeatherAPI(context, new VolleyListener(), new AsyncListener());
        initializeAPIFragment();
        initializeDifferenceFragment();
        initializeMapsFragment();
        initializeSensorFragment();
    }

    private void initializeAPIFragment() {
        apiFragment = (APIFragment) context.getSupportFragmentManager().findFragmentByTag(API_FRAGMENT_TAG);
        if (apiFragment == null){
            apiFragment = new APIFragment();
        }
        apiFragment.setController(this);
    }

    private void initializeDifferenceFragment() {
        differenceFragment = (DifferenceFragment) context.getSupportFragmentManager().findFragmentByTag(DIFFERENCE_FRAGMENT_TAG);
        if (differenceFragment == null){
            differenceFragment = new DifferenceFragment();
        }
        differenceFragment.setController(this);
    }

    private void initializeMapsFragment() {
        mapsFragment = (MapsFragment) context.getSupportFragmentManager().findFragmentByTag(MAPS_FRAGMENT_TAG);
        if (mapsFragment == null){
            mapsFragment = new MapsFragment();
        }
        mapsFragment.setController(this);
    }

    private void initializeSensorFragment() {
        sensorFragment = (SensorFragment) context.getSupportFragmentManager().findFragmentByTag(SENSOR_FRAGMENT_TAG);
        if (sensorFragment == null){
            sensorFragment = new SensorFragment();
            sensorFragment.setController(this);
        }
    }

    public void showMapFragment() {
        context.setFragment(mapsFragment,MAPS_FRAGMENT_TAG,false);
    }

    public void showSensorFragment() {
        context.setFragment(sensorFragment,SENSOR_FRAGMENT_TAG,false);
    }

    public void showAPIFragment() {
        context.setFragment(apiFragment,API_FRAGMENT_TAG,false);
    }

    public void showDifferenceFragment(){
        context.setFragment(differenceFragment,DIFFERENCE_FRAGMENT_TAG,false);
    }

    public void setLocation(LatLng location) {
        this.location = location;
        requestOpenWeatherData("volley");
        showAPIFragment();
    }

    public void requestOpenWeatherData(String type) {
        if (location != null){
            openWeatherAPI.sendWeatherDataRequest(type,location);
        } else {
            Toast.makeText(context, "No location is set", Toast.LENGTH_LONG);
        }
    }
    
    private String[] parseJSONWeatherData(JSONObject json) throws JSONException {
        String [] toReturn = new String [5];
        JSONArray weatherArray = json.getJSONArray("weather");
        toReturn[0] = "" + weatherArray.getJSONObject(4).getInt("icon");
        JSONArray main = json.getJSONArray("main");
        toReturn[1] = "" + main.getJSONObject(1).getDouble("pressure");
        toReturn[2] = "" + (main.getJSONObject(0).getDouble("temp") - 273.15) + "°C";
        toReturn[3] = main.getJSONObject(2).getString("humidity");
        toReturn[4] = null; //calculate altitude;
        return toReturn;
    }

    class VolleyListener implements Response.Listener<JSONObject>, Response.ErrorListener{
        private static final String TAG = "via Volley";

        @Override
        public void onResponse(JSONObject response) {
            try {
                Log.d(TAG, "onResponse: " + response.toString());
                String [] data = parseJSONWeatherData(response);
                int weatherImageResource = Integer.parseInt(data[0]);
                String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
                apiFragment.setValues(weatherImageResource,data[1],data[2],data[3],data[4],timeStamp,TAG);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(context, "Could receive data", Toast.LENGTH_SHORT).show();
            error.printStackTrace();
        }
    }

    class AsyncListener extends AsyncTask<String, Void, JSONObject> {
        private static final String TAG = "via AsyncTask";
        @Override
        protected JSONObject doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream input = connection.getInputStream();
                String inString = new Scanner(input, "UTF-8").useDelimiter("\\A").next();
                input.close();
                JSONObject response = new JSONObject(inString);
//                Log.d(TAG, "doInBackground: " + response.toString());
                return response;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            if (response != null){
                try {
                    Log.d(TAG, "onResponse: " + response.toString());
                    String [] data = parseJSONWeatherData(response);
                    int weatherImageResource = Integer.parseInt(data[0]);
                    String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
                    apiFragment.setValues(weatherImageResource,data[1],data[2],data[3],data[4],timeStamp,TAG);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, "Could receive data", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
