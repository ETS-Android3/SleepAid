package com.example.sleepaid.MainMenu.Fragment.SleepData;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sleepaid.CircleBox;
import com.example.sleepaid.DataHandler;
import com.example.sleepaid.R;
import com.example.sleepaid.TextBox;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BedtimeGraphFragment extends SleepDataGraphFragment {
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
        super.loadFromDatabase("Bedtime");
    }

    protected void loadTodaysData() {
//        TextBox durationBox = sleepDataFragment.getView().findViewById(R.id.rightBox);
//        durationBox.setText(sleepDataFragment.todayDuration);
//
//        TextBox wakeupTimeBox = sleepDataFragment.getView().findViewById(R.id.leftBox);
//        wakeupTimeBox.setText(sleepDataFragment.todayWakeupTime);

        CircleBox bedtimeBox = sleepDataFragment.getView().findViewById(R.id.middleBox);
        bedtimeBox.setText(sleepDataFragment.todayBedtime);
    }
}