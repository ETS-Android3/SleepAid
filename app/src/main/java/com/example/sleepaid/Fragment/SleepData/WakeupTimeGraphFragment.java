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

public class WakeupTimeGraphFragment extends SleepDataGraphFragment {
    protected void loadGraph(ZonedDateTime min, ZonedDateTime max) {
        super.loadGraph(min, max);
        super.loadGoal("Wake-up time");
    }

    protected void loadTodaysData() {
//        TextBox durationBox = sleepDataFragment.getView().findViewById(R.id.leftBox);
//        durationBox.setText(sleepDataFragment.todayDuration);

        CircleBox wakeupTimeBox = sleepDataFragment.getView().findViewById(R.id.middleBox);
        wakeupTimeBox.setText(sleepDataFragment.todayWakeupTime);

//        TextBox bedTimeBox = sleepDataFragment.getView().findViewById(R.id.rightBox);
//        bedTimeBox.setText(sleepDataFragment.todayBedTime);
    }
}