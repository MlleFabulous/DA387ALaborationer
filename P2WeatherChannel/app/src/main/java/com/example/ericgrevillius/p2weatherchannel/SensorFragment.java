package com.example.ericgrevillius.p2weatherchannel;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class SensorFragment extends Fragment {


    private Controller controller;

    public SensorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sensor, container, false);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }
}
