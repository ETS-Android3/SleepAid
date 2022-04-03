package com.example.sleepaid.Fragment.SleepData;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.sleepaid.App;
import com.example.sleepaid.R;

public class SleepDurationGraphFragment extends SleepDataGraphFragment {
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        this.fieldName = "Sleep duration";
        this.graphColor = ContextCompat.getColor(App.getContext(), R.color.purple_sleep_15_transparent);

        super.onViewCreated(view, savedInstanceState);
    }
}