package com.example.ericgrevillius.p4pathfinder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ericgrevillius.p4pathfinder.database.StepSession;
import java.util.Calendar;
import java.util.List;

public class SessionHistoryAdapter extends ArrayAdapter<StepSession> {
    private LayoutInflater inflater;
    private List<StepSession> stepSessions;

    public SessionHistoryAdapter(@NonNull Context context, List<StepSession> sessions) {
        super(context, android.R.layout.simple_list_item_1);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        stepSessions = sessions;
    }

    @Override
    public int getCount() {
        return stepSessions.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView textView;
        if (convertView == null){
            textView = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, parent,false);
        } else {
            textView = (TextView) convertView;
        }
        StepSession session = stepSessions.get(position);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(session.getDate());
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        String message = year + "-" + month + "-" + day + "\n" +
                "Steps taken: " + session.getSteps().size();
        textView.setText(message);
        return textView;
    }

    public StepSession getStepSession(int index){
        return stepSessions.get(index);
    }
}
