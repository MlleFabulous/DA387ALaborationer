package com.example.ericgrevillius.p2weatherchannel;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

public class OpenWeatherAPI {
    private static final String TAG = "OpenWeatherAPI";
    private static final String URL = "api.openweathermap.org/data/2.5/weather?";
    private static final String URL_LAT_EXT = "lat=";
    private String lat;
    private static final String URL_LONG_EXT = "&lon=";
    private String lng;
    private static final String URL_KEY_EXT = "&APPID=";
    private static final String API_KEY = "0bd96e0c4735308a0af0ca40dc18fd40";
    private RequestQueue requestQueue;

    public JSONObject getWeatherData(String type, LatLng latLng){
        lat = "" + latLng.latitude;
        lng = "" + latLng.longitude;
        JSONObject json = null;
        switch (type){
            case "volley":
                json = volleyWeatherDataRequest();
                break;
            case "async":
                json = asyncTaskWeatherRequest();
                break;
        }
        return json;
    }

    private JSONObject asyncTaskWeatherRequest() {
        JSONObject json = null;

        return json;
    }

    private JSONObject volleyWeatherDataRequest() {
        JSONObject json = null;
        String completeURL = URL + URL_LAT_EXT + lat + URL_LONG_EXT + lng + URL_KEY_EXT + API_KEY;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, completeURL, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        return json;
    }
}
