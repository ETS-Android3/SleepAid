package com.example.sleepaid.Fragment.SleepData;

import com.example.sleepaid.Component.CircleBox;
import com.example.sleepaid.R;

import java.time.ZonedDateTime;

public class BedtimeGraphFragment extends SleepDataGraphFragment {
    protected void loadGraph(ZonedDateTime min, ZonedDateTime max) {
        super.loadGraph(min, max);
        super.loadGoal("Bedtime");
    }

    protected void loadTodayData() {
//        TextBox durationBox = sleepDataFragment.getView().findViewById(R.id.rightBox);
//        durationBox.setText(sleepDataFragment.todayDuration);
//
//        TextBox wakeupTimeBox = sleepDataFragment.getView().findViewById(R.id.leftBox);
//        wakeupTimeBox.setText(sleepDataFragment.todayWakeupTime);

        CircleBox bedtimeBox = sleepDataFragment.getView().findViewById(R.id.middleBox);
        bedtimeBox.setText(sleepDataFragment.todayBedtime);
    }
}