package com.example.ericgrevillius.p4pathfinder;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;

import com.example.ericgrevillius.p4pathfinder.database.StepSession;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class SessionHistoryFragment extends Fragment {
    private static final String TAG = "SessionHistoryFragment";
    private UserController controller;
    private ListView sessionListView;
    private SessionHistoryAdapter adapter;
    private Button fromDateButton;
    private Button toDateButton;
    private Button clearSessionsButton;
    private Calendar calendar;
    private long toDate;
    private long fromDate;


    public SessionHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_session_history, container, false);
        initializeComponents(view, savedInstanceState);
        return view;
    }

    private void initializeComponents(View view, Bundle savedInstanceState) {
        sessionListView = view.findViewById(R.id.history_list_view);
        if (adapter != null){
            sessionListView.setAdapter(adapter);
        }
        sessionListView.setOnItemClickListener(new ItemListener());
        ButtonListener buttonListener = new ButtonListener();
        fromDateButton = view.findViewById(R.id.history_from_date_button);
        fromDateButton.setOnClickListener(buttonListener);
        toDateButton = view.findViewById(R.id.history_to_date_button);
        toDateButton.setOnClickListener(buttonListener);
        clearSessionsButton = view.findViewById(R.id.history_clear_history_button);
        clearSessionsButton.setOnClickListener(buttonListener);
    }

    public void setController(UserController controller) {
        this.controller = controller;
    }

    public void setAdapter(SessionHistoryAdapter adapter) {
        this.adapter = adapter;
        if (sessionListView != null){
            sessionListView.setAdapter(adapter);
        }
    }

    private void formatDate() {
        toDate = -1;
        fromDate = -1;
        calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", getResources().getConfiguration().locale);
        try {
            calendar.setTime(sdf.parse(toDateButton.getText().toString()));
            toDate = calendar.getTimeInMillis();
        } catch (ParseException e) {
            Log.d(TAG, "Can't parse toDate: " + toDateButton.getText().toString());
            toDate = -1;
        }
        try {
            calendar.setTime(sdf.parse(fromDateButton.getText().toString()));
            fromDate = calendar.getTimeInMillis();
        } catch (ParseException e) {
            Log.d(TAG, "Can't parse fromDate: " + fromDateButton.getText().toString());
            fromDate = -1;
        }
        controller.searchForSessions(fromDate, toDate);
    }

    private class ButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            final int id = view.getId();
            if (id == clearSessionsButton.getId()){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle(R.string.warning_text);
                alertDialogBuilder.setMessage("Are you sure you want to delete everything? It will be lost forever." +
                        "\n(a very long time)");
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        controller.deleteAllSessions();
                        formatDate();
                        dialogInterface.dismiss();
                    }
                });
                alertDialogBuilder.create().show();
            } else if (id == fromDateButton.getId() || id == toDateButton.getId()) {
                calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        String date = year + "-" + (month+1) + "-" + day;
                        if (id == toDateButton.getId()){
                            toDateButton.setText(date);
                        } else if (id == fromDateButton.getId()){
                            fromDateButton.setText(date);
                        }
                        formatDate();
                    }
                }, year, month, day);
                datePickerDialog.updateDate(year, month, day);
                datePickerDialog.show();
            }
        }
    }

    private class ItemListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            StepSession session = adapter.getStepSession(i);
            controller.displaySessionDialogFragment(false, session.getSessionID());
        }
    }
}
