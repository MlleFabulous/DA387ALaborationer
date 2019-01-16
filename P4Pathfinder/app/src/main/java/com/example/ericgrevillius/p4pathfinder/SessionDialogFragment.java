package com.example.ericgrevillius.p4pathfinder;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.ericgrevillius.p4pathfinder.database.StepSession;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class SessionDialogFragment extends DialogFragment {
    private static final String TAG = "SessionDialogFragment";
    private UserController controller;
    private StepSession session;
    private TextView dateTextView;
    private String date;
    private TextView totalStepsTextView;
    private String totalSteps;
    private TextView walkingStepTextView;
    private String walkingSteps;
    private TextView walkingPercentTextView;
    private String walkingPercent;
    private TextView runningStepTextView;
    private String runningSteps;
    private TextView runningPercentTextView;
    private String runningPercent;
    private TextView movementCommentTextView;
    private String movementComment;
    private Button closeButton;
    private boolean isServiceActive;
    private long sessionID;
    private Thread thread;

    public SessionDialogFragment() {
        // Required empty public constructor
    }

    public static SessionDialogFragment newInstance(boolean isServiceActive, long sessionID) {
        Bundle args = new Bundle();
        args.putBoolean("isServiceActive", isServiceActive);
        args.putLong("sessionID", sessionID);
        SessionDialogFragment fragment = new SessionDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_session_dialog, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeComponents(view,savedInstanceState);
        initializeThread();
    }

    private void initializeComponents(View view, Bundle savedInstanceState) {
        dateTextView = view.findViewById(R.id.session_dialog_date_text_view);
        totalStepsTextView = view.findViewById(R.id.session_dialog_total_steps_text_view);
        walkingStepTextView = view.findViewById(R.id.session_dialog_walking_text_view);
        walkingPercentTextView = view.findViewById(R.id.session_dialog_walking_percent_text_view);
        runningStepTextView = view.findViewById(R.id.session_dialog_running_text_view);
        runningPercentTextView = view.findViewById(R.id.session_dialog_runnng_percent_text_view);
        movementCommentTextView = view.findViewById(R.id.session_dialog_movement_comment_text_view);
        closeButton = view.findViewById(R.id.session_dialog_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isServiceActive = false;
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dismiss();
            }
        });
        if (savedInstanceState != null){
            date = savedInstanceState.getString("date");
            dateTextView.setText(date);
            totalSteps = savedInstanceState.getString("totalSteps");
            totalStepsTextView.setText(totalSteps);
            walkingSteps = savedInstanceState.getString("walkingSteps");
            walkingStepTextView.setText(walkingSteps);
            walkingPercent = savedInstanceState.getString("walkingPercent");
            walkingPercentTextView.setText(walkingPercent);
            runningSteps = savedInstanceState.getString("runningSteps");
            runningStepTextView.setText(runningSteps);
            runningPercent = savedInstanceState.getString("runningPercent");
            runningPercentTextView.setText(runningPercent);
            movementComment = savedInstanceState.getString("movementComment");
            movementCommentTextView.setText(movementComment);
        }
    }

    private void initializeThread() {
        Bundle args = getArguments();
        if (args != null){
            isServiceActive = args.getBoolean("isServiceActive");
            sessionID = args.getLong("sessionID");
            thread = new Thread(new InformationTask());
            thread.start();
        }
    }

    public void setController(UserController controller) {
        this.controller = controller;
    }

    public void setSession(StepSession session) {
        this.session = session;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(session.getDate());
        date = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
        if (dateTextView != null){
            dateTextView.setText(date);
        }
        totalSteps = "Total steps: " + session.getSteps();
        if (totalStepsTextView != null){
            totalStepsTextView.setText(totalSteps);
        }
        walkingSteps = session.getWalkedSteps() + " steps.";
        if (walkingStepTextView != null){
            walkingStepTextView.setText(walkingSteps);
        }
        double percent;
        try {
            percent = ((double)session.getWalkedSteps() / (double)session.getSteps())*100.0;
        } catch (ArithmeticException e){
            percent = 0.0;
        }
        if (getActivity() != null){
            walkingPercent = String.format(getActivity().getResources().getConfiguration().locale,"%.1f", percent) + " %";
        }
        if (walkingPercentTextView != null){
            walkingPercentTextView.setText(walkingPercent);
        }
        runningSteps = session.getRunSteps() + " steps.";
        if (runningStepTextView != null){
            runningStepTextView.setText(runningSteps);
        }
        try {
            percent = ((double)session.getRunSteps() / (double)session.getSteps()) * 100.0;
        } catch (ArithmeticException e){
            //percent is already 0
        }
        if (getActivity() != null){
            runningPercent = String.format(getActivity().getResources().getConfiguration().locale,"%.1f", percent) + " %";
        }
        if (runningPercentTextView != null){
            runningPercentTextView.setText(runningPercent);
        }
        if (percent > 80.0){
            movementComment = "You're awesome!";
        } else if (percent > 60.0){
            movementComment = "Good job!";
        } else if (percent > 40.0){
            movementComment = "Not bad!";
        } else if (percent > 20.0){
            movementComment = "*heavy sigh* Try again.";
        } else {
            movementComment = "You're a couch potato!";
        }
        if (movementCommentTextView != null){
            movementCommentTextView.setText(movementComment);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("date",date);
        outState.putString("totalSteps",totalSteps);
        outState.putString("walkingSteps",walkingSteps);
        outState.putString("walkingPercent",walkingPercent);
        outState.putString("runningSteps",runningSteps);
        outState.putString("runningPercent",runningPercent);
        outState.putString("movementComment",movementComment);
        super.onSaveInstanceState(outState);
    }

    private class InformationTask implements Runnable{
        @Override
        public void run() {
            do{
                try {
                    controller.getSessionInformation(sessionID);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (isServiceActive);
        }
    }
}
