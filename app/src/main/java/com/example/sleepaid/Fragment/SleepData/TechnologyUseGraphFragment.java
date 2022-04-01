package com.example.sleepaid.Fragment.SleepData;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.sleepaid.App;
import com.example.sleepaid.Component.CircleBox;
import com.example.sleepaid.R;

import java.time.ZonedDateTime;

public class TechnologyUseGraphFragment extends SleepDataGraphFragment {
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        this.fieldName = "Technology use";
        this.graphColor = ContextCompat.getColor(App.getContext(), R.color.purple_sleep_transparent);

        super.onViewCreated(view, savedInstanceState);
    }
}