package com.example.ericgrevillius.p2weatherchannel;

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

    public Controller(MainActivity activity){
        context = activity;
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
}
