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
import java.util.Calendar;
import java.util.Locale;
import java.util.Scanner;

class Controller {
    //    private static final String TAG = "Controller";
    private static final String API_FRAGMENT_TAG = "APIFragment";
    private static final String DIFFERENCE_FRAGMENT_TAG = "DifferenceFragment";
    private static final String MAPS_FRAGMENT_TAG = "MapsFragment";
    private static final String SENSOR_FRAGMENT_TAG = "SensorFragment";
    private APIFragment apiFragment;
    private DifferenceFragment differenceFragment;
    private MapsFragment mapsFragment;
    private SensorFragment sensorFragment;
    private MainActivity context;
    private OpenWeatherAPI openWeatherAPI;
    private LatLng location;
    private String respondTo;

    Controller(MainActivity activity, String currentFragmentTag) {
        context = activity;
        openWeatherAPI = new OpenWeatherAPI(context, new VolleyListener());
        initializeAPIFragment();
        initializeDifferenceFragment();
        initializeMapsFragment();
        initializeSensorFragment();
        if (currentFragmentTag != null) {
            switch (currentFragmentTag) {
                case API_FRAGMENT_TAG:
                    showAPIFragment(false);
                    break;
                case DIFFERENCE_FRAGMENT_TAG:
                    showDifferenceFragment(false);
                    break;
                case SENSOR_FRAGMENT_TAG:
                    showSensorFragment(false);
                    break;
                default:
                    showMapFragment(false);
            }
        } else {
            showMapFragment(false);
        }
    }

    private void initializeAPIFragment() {
        apiFragment = (APIFragment) context.getSupportFragmentManager().findFragmentByTag(API_FRAGMENT_TAG);
        if (apiFragment == null) {
            apiFragment = new APIFragment();
        }
        apiFragment.setController(this);
    }

    private void initializeDifferenceFragment() {
        differenceFragment = (DifferenceFragment) context.getSupportFragmentManager().findFragmentByTag(DIFFERENCE_FRAGMENT_TAG);
        if (differenceFragment == null) {
            differenceFragment = new DifferenceFragment();
        }
        differenceFragment.setController(this);
    }

    private void initializeMapsFragment() {
        mapsFragment = (MapsFragment) context.getSupportFragmentManager().findFragmentByTag(MAPS_FRAGMENT_TAG);
        if (mapsFragment == null) {
            mapsFragment = new MapsFragment();
        }
        mapsFragment.setController(this);
    }

    private void initializeSensorFragment() {
        sensorFragment = (SensorFragment) context.getSupportFragmentManager().findFragmentByTag(SENSOR_FRAGMENT_TAG);
        if (sensorFragment == null) {
            sensorFragment = new SensorFragment();
        }
    }

    void showMapFragment(boolean backStack) {
        context.setFragment(mapsFragment, MAPS_FRAGMENT_TAG, backStack);
    }

    void showSensorFragment(boolean backStack) {
        context.setFragment(sensorFragment, SENSOR_FRAGMENT_TAG, backStack);
    }

    void showAPIFragment(boolean backStack) {
        if (location != null) {
            requestOpenWeatherData("volley", API_FRAGMENT_TAG);
        }
        context.setFragment(apiFragment, API_FRAGMENT_TAG, backStack);
    }

    void showDifferenceFragment(boolean backStack) {
        context.setFragment(differenceFragment, DIFFERENCE_FRAGMENT_TAG, backStack);
    }

    void setLocation(LatLng location, String respondTo) {
        this.location = location;
        if (respondTo.matches(MAPS_FRAGMENT_TAG) || respondTo.matches(API_FRAGMENT_TAG)) {
            showAPIFragment(false);
        } else {
            requestOpenWeatherData("volley", respondTo);
        }
    }

    void requestOpenWeatherData(String type, String respondTo) {
        this.respondTo = respondTo;
        if (location != null) {
            if (type.matches("async")) {
                openWeatherAPI.setNewAsynctaskListener(new AsyncTaskListener());
            }
            openWeatherAPI.sendWeatherDataRequest(type, location);
        } else {
            Toast.makeText(context, "No location is set", Toast.LENGTH_LONG).show();
        }
    }

    private String[] parseJSONWeatherData(JSONObject json) throws JSONException {
        String[] toReturn = new String[6];
        JSONObject coord = json.getJSONObject("coord");
        toReturn[0] = "@ Lat: " + coord.getDouble("lat") + ", Long: " + coord.getDouble("lon");
        JSONArray weatherArray = json.getJSONArray("weather");
        toReturn[1] = weatherArray.getJSONObject(0).getString("description");
        toReturn[2] = "" + weatherArray.getJSONObject(0).getString("icon");
        JSONObject main = json.getJSONObject("main");
        toReturn[3] = "" + main.getDouble("pressure");
        double temperature = main.getDouble("temp") - 273.15;
        toReturn[4] = String.format(Locale.getDefault(), "%.1f", temperature);
        toReturn[5] = "" + main.getDouble("humidity");
        return toReturn;
    }

    private int findImageResource(String weather) {
        switch (weather) {
            case "01d":
                return R.drawable.a01d;
            case "02d":
                return R.drawable.a02d;
            case "03d":
                return R.drawable.a03d;
            case "04d":
                return R.drawable.a04d;
            case "09d":
                return R.drawable.a09d;
            case "10d":
                return R.drawable.a10d;
            case "11d":
                return R.drawable.a11d;
            case "13d":
                return R.drawable.a13d;
            case "50d":
                return R.drawable.a50d;
            case "01n":
                return R.drawable.a01n;
            case "02n":
                return R.drawable.a02n;
            case "03n":
                return R.drawable.a03n;
            case "04n":
                return R.drawable.a04n;
            case "09n":
                return R.drawable.a09n;
            case "10n":
                return R.drawable.a10n;
            case "11n":
                return R.drawable.a11n;
            case "13n":
                return R.drawable.a13n;
            case "50n":
                return R.drawable.a50n;
        }
        return -1;
    }

    class VolleyListener implements Response.Listener<JSONObject>, Response.ErrorListener {
        private static final String TAG = "via Volley";

        @Override
        public void onResponse(JSONObject response) {
            try {
                Log.d(TAG, "onResponse: " + response.toString());
                final String[] data = parseJSONWeatherData(response);
                final int weatherImageResource = findImageResource(data[2]);
                final String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
                switch (respondTo) {
                    case MAPS_FRAGMENT_TAG:
                    case API_FRAGMENT_TAG:
                        apiFragment.setValues(data[0], data[1], weatherImageResource, data[3], data[4], data[5], timeStamp, TAG);
//                        showAPIFragment();
                        break;
                    case DIFFERENCE_FRAGMENT_TAG:
                        differenceFragment.setValues(data[0], data[3], data[4], data[5]);
                        break;
                }
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

    class AsyncTaskListener extends AsyncTask<String, Void, JSONObject> {
        private static final String TAG = "via AsyncTask";

        @Override
        protected JSONObject doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream input = connection.getInputStream();
                String inString = new Scanner(input, "UTF-8").useDelimiter("\\A").next();
                input.close();
                return new JSONObject(inString);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            if (response != null) {
                try {
                    Log.d(TAG, "onResponse: " + response.toString());
                    final String[] data = parseJSONWeatherData(response);
                    final int weatherImageResource = findImageResource(data[2]);
                    final String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
                    switch (respondTo) {
                        case MAPS_FRAGMENT_TAG:
                        case API_FRAGMENT_TAG:
//                            showAPIFragment();
                            apiFragment.setValues(data[0], data[1], weatherImageResource, data[3], data[4], data[5], timeStamp, TAG);
                            break;
                        case DIFFERENCE_FRAGMENT_TAG:
                            differenceFragment.setValues(data[0], data[3], data[4], data[5]);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, "Could receive data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
