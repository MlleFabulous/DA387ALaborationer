package com.example.ericgrevillius.p2weatherchannel;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


public class MapsFragment extends Fragment implements OnMapReadyCallback, LocationListener {
    private static final String TAG = "MapsFragment";
    private Controller controller;
    private MapView mapView;
    private GoogleMap map;
    private ArrayList<MarkerOptions> markers = new ArrayList<>();
    private Bundle savedInstanceState;
    private LocationManager locationManager;
    private Location lastLocation;
    private Marker currentMarker;


    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_maps, container, false);
        this.savedInstanceState = savedInstanceState;
        initializeLocation();
        initializeMap(view);
        return view;
    }

    private void initializeLocation() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    private void initializeMap(View view) {
        mapView = view.findViewById(R.id.mapView);
        if (mapView != null){
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    private void addMarker(LatLng latLng){
        MarkerOptions mo;
        mo = new MarkerOptions().position(latLng);
        markers.add(mo);
        map.addMarker(mo);
    }

    private void clearMarkers(){
        markers.clear();
        map.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        }
    }

    @Override
    public void onPause() {
        locationManager.removeUpdates(this);
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("markers",markers);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                addMarker(latLng);
            }
        });
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                controller.setLocation(marker.getPosition(), TAG);
                return false;
            }
        });

        if (savedInstanceState != null){
            markers = savedInstanceState.getParcelableArrayList("markers");
            for(MarkerOptions mo : markers) {
                map.addMarker(mo);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        LatLng latLng = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
        if (currentMarker != null) {
            currentMarker.remove();
        }
        MarkerOptions mo = new MarkerOptions().position(latLng).title("My position");
        currentMarker = map.addMarker(mo);
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
