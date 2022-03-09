package com.example.sleepaid.Fragment.SleepData;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sleepaid.Component.CircleBox;
import com.example.sleepaid.R;

import java.util.Date;

public class SleepDurationGraphFragment extends SleepDataGraphFragment {
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sleep_data_graph, container, false);
    }

    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    protected void loadGraph(Date min, Date max) {
        super.loadGraph(min, max);
        super.loadGoal("Sleep duration");
    }

    protected void loadTodaysData() {
        CircleBox durationBox = sleepDataFragment.getView().findViewById(R.id.middleBox);
        durationBox.setText(sleepDataFragment.todayDuration);

//        TextBox wakeupTimeBox = sleepDataFragment.getView().findViewById(R.id.leftBox);
//        wakeupTimeBox.setText(sleepDataFragment.todayWakeupTime);
//
//        TextBox bedTimeBox = sleepDataFragment.getView().findViewById(R.id.rightBox);
//        bedTimeBox.setText(sleepDataFragment.todayBedTime);
    }
}