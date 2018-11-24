package com.example.ericgrevillius.p2weatherchannel;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

public class OpenWeatherAPI {
    private static final String TAG = "OpenWeatherAPI";
    private static final String URL = "http://api.openweathermap.org/data/2.5/weather?";
    private static final String URL_LAT_EXT = "lat=";
    private String lat;
    private static final String URL_LONG_EXT = "&lon=";
    private String lng;
    private static final String URL_KEY_EXT = "&APPID=";
    private static final String API_KEY = "0bd96e0c4735308a0af0ca40dc18fd40";
    private RequestQueue requestQueue;
    private Controller.VolleyListener volleyListener;
    private Controller.AsyncTaskListener asyncTaskListener;

    public OpenWeatherAPI(Context context, Controller.VolleyListener volleyListener) {
        requestQueue = Volley.newRequestQueue(context);
        this.volleyListener = volleyListener;
    }

    public void sendWeatherDataRequest(String type, LatLng latLng){
        lat = "" + latLng.latitude;
        lng = "" + latLng.longitude;
        switch (type){
            case "volley":
                volleyWeatherDataRequest();
                break;
            case "async":
                asyncTaskWeatherRequest();
                break;
        }
    }

    private void asyncTaskWeatherRequest() {
        String url = URL + URL_LAT_EXT + lat + URL_LONG_EXT + lng + URL_KEY_EXT + API_KEY;
        asyncTaskListener.execute(url);
    }

    private void volleyWeatherDataRequest() {
        String url = URL + URL_LAT_EXT + lat + URL_LONG_EXT + lng + URL_KEY_EXT + API_KEY;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(), volleyListener, volleyListener );
        requestQueue.add(request);
    }

    public void setNewAsynctaskListener(Controller.AsyncTaskListener newAsynctaskListener) {
        this.asyncTaskListener = newAsynctaskListener;
    }
}
