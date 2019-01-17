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

import com.example.ericgrevillius.p4pathfinder.database.Step;
import com.example.ericgrevillius.p4pathfinder.database.StepSession;

import java.util.Calendar;
import java.util.List;


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
    private TextView stepsPerSecondTextView;
    private String stepsPerSecond;
    private TextView walkingStepTextView;
    private String walkingSteps;
    private TextView walkingPercentTextView;
    private String walkingPercentString;
    private TextView runningStepTextView;
    private String runningSteps;
    private TextView runningPercentTextView;
    private String runningPercentString;
    private TextView movementCommentTextView;
    private String movementComment;
    private Button closeButton;
    private boolean isServiceActive;
    private long sessionID;
    private double walkingPercent;
    private double runningPercent;
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
        initializeComponents(view, savedInstanceState);
        initializeThread();
    }

    private void initializeComponents(View view, Bundle savedInstanceState) {
        dateTextView = view.findViewById(R.id.session_dialog_date_text_view);
        totalStepsTextView = view.findViewById(R.id.session_dialog_total_steps_text_view);
        stepsPerSecondTextView = view.findViewById(R.id.session_dialog_steps_per_second_text_view);
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
        if (savedInstanceState != null) {
            date = savedInstanceState.getString("date");
            dateTextView.setText(date);
            totalSteps = savedInstanceState.getString("totalSteps");
            totalStepsTextView.setText(totalSteps);
            stepsPerSecond = savedInstanceState.getString("stepsPerSecond");
            stepsPerSecondTextView.setText(stepsPerSecond);
            walkingSteps = savedInstanceState.getString("walkingSteps");
            walkingStepTextView.setText(walkingSteps);
            walkingPercentString = savedInstanceState.getString("walkingPercentString");
            walkingPercentTextView.setText(walkingPercentString);
            runningSteps = savedInstanceState.getString("runningSteps");
            runningStepTextView.setText(runningSteps);
            runningPercentString = savedInstanceState.getString("runningPercentString");
            runningPercentTextView.setText(runningPercentString);
            movementComment = savedInstanceState.getString("movementComment");
            movementCommentTextView.setText(movementComment);
        }
    }

    private void initializeThread() {
        Bundle args = getArguments();
        if (args != null) {
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
        if (getActivity() != null) {
            updateDate();
            updateTotalSteps();
            updateStepsPerSecond();
            updateWalkingSteps();
            updateWalkingPercent();
            updateRunningSteps();
            updateRunningPercent();
            updateMovementComment();
        }
    }

    private void updateDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(session.getDate());
        date = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
        if (dateTextView != null) {
            dateTextView.setText(date);
        }
    }

    private void updateTotalSteps() {
        totalSteps = "Total steps: " + session.getSteps().size();
        if (totalStepsTextView != null) {
            totalStepsTextView.setText(totalSteps);
        }
    }

    private void updateStepsPerSecond() {
        List<Step> steps = session.getSteps();
        if (steps.size() > 0) {
            long end = isServiceActive ? Calendar.getInstance().getTimeInMillis() : steps.get(steps.size() - 1).getDate();
            long timeIntervalInSeconds = (end - steps.get(0).getDate()) / 1000;
            double SPS = (double) session.getSteps().size() / timeIntervalInSeconds;
            stepsPerSecond = String.format(getActivity().getResources().getConfiguration().locale, "%.1f", SPS) + " steps/second";
            if (stepsPerSecondTextView != null) {
                stepsPerSecondTextView.setText(stepsPerSecond);
            }
        }
    }

    private void updateWalkingSteps() {
        walkingSteps = session.getWalkedSteps() + " steps.";
        if (walkingStepTextView != null) {
            walkingStepTextView.setText(walkingSteps);
        }
    }

    private void updateWalkingPercent() {
        try {
            walkingPercent = ((double) session.getWalkedSteps() / (double) session.getSteps().size()) * 100.0;
        } catch (ArithmeticException e) {
            walkingPercent = 0.0;
        }
        walkingPercentString = String.format(getActivity().getResources().getConfiguration().locale, "%.1f", walkingPercent) + " %";
        if (walkingPercentTextView != null) {
            walkingPercentTextView.setText(walkingPercentString);
        }

    }

    private void updateRunningSteps() {
        runningSteps = session.getRunSteps() + " steps.";
        if (runningStepTextView != null) {
            runningStepTextView.setText(runningSteps);
        }
    }

    private void updateRunningPercent() {
        try {
            runningPercent = ((double) session.getRunSteps() / (double) session.getSteps().size()) * 100.0;
        } catch (ArithmeticException e) {
            //runningPercent is already 0
        }
        runningPercentString = String.format(getActivity().getResources().getConfiguration().locale, "%.1f", runningPercent) + " %";
        if (runningPercentTextView != null) {
            runningPercentTextView.setText(runningPercentString);
        }
    }

    private void updateMovementComment() {
        if (runningPercent > 80.0) {
            movementComment = "You're awesome!";
        } else if (runningPercent > 60.0) {
            movementComment = "Good job!";
        } else if (runningPercent > 40.0) {
            movementComment = "Not bad!";
        } else if (runningPercent > 20.0) {
            movementComment = "*heavy sigh* Try again.";
        } else {
            movementComment = "You're a couch potato!";
        }
        if (movementCommentTextView != null) {
            movementCommentTextView.setText(movementComment);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("date", date);
        outState.putString("totalSteps", totalSteps);
        outState.putString("stepsPerSecond", stepsPerSecond);
        outState.putString("walkingSteps", walkingSteps);
        outState.putString("walkingPercentString", walkingPercentString);
        outState.putString("runningSteps", runningSteps);
        outState.putString("runningPercentString", runningPercentString);
        outState.putString("movementComment", movementComment);
        super.onSaveInstanceState(outState);
    }

    private class InformationTask implements Runnable {
        @Override
        public void run() {
            do {
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
