package com.example.ericgrevillius.p4pathfinder;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class StepServiceConnection implements ServiceConnection {
    private final CurrentSessionFragment fragment;

    public StepServiceConnection(CurrentSessionFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        StepService.LocalBinder binder = (StepService.LocalBinder) iBinder;
        fragment.service = binder.getService();
        fragment.isServiceBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        fragment.isServiceBound = false;
    }
}
