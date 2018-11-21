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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    class VolleyListener implements Response.Listener<JSONObject>, Response.ErrorListener{

        @Override
        public void onResponse(JSONObject response) {
            try {
                //TODO: use response
                Log.d(TAG, "onResponse: " + response.toString());
                JSONArray weatherArray = response.getJSONArray("weather");
                int weatherImageResource = weatherArray.getJSONObject(4).getInt("icon");
                JSONArray main = response.getJSONArray("main");
                String pressure = "" + main.getJSONObject(1).getDouble("pressure");
                String temperature = "" + (main.getJSONObject(0).getDouble("temp") - 273.15) + "°C";
                String humidity = main.getJSONObject(2).getString("humidity");
//                String timeStamp =
//                apiFragment.setValues();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            //TODO: Handle error response
            error.printStackTrace();
        }
    }

    class AsyncListener extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream input = connection.getInputStream();
                String inString = new Scanner(input, "UTF-8").useDelimiter("\\A").next();
                input.close();
//                JsonParser parser = new JsonParser();
//                JsonObject json = (JsonObject)parser.parse(inString);
//                Gson gson = new Gson();
                JSONObject response = new JSONObject(inString);
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
                //TODO: use response
            } else {
                //TODO: handle Error response
            }
        }
    }
}
