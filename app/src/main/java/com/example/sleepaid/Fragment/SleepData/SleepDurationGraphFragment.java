package com.example.sleepaid.Fragment.SleepData;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sleepaid.Component.CircleBox;
import com.example.sleepaid.R;

import java.time.ZonedDateTime;

public class SleepDurationGraphFragment extends SleepDataGraphFragment {
    protected void loadGraph(ZonedDateTime min, ZonedDateTime max) {
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