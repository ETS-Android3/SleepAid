package com.example.sleepaid.Fragment.Alarms;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NapAlarmsFragment extends AlarmListFragment {
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.loadAlarmList(2);
    }
}