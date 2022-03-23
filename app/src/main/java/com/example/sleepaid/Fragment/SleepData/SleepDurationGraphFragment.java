package com.example.sleepaid.Fragment.SleepData;

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