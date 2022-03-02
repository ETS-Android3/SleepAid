package com.example.sleepaid.MainMenu.Fragment.Alarms;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sleepaid.App;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

@SuppressLint("NewApi")
public class AlarmConfigurationScreenFragment extends Fragment implements View.OnClickListener {
    protected AppDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alarm_configuration_screen, container, false);
    }

    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        db = AppDatabase.getDatabase(App.getContext());

        Button cancelAlarmConfigurationButton = view.findViewById(R.id.cancelAlarmConfigurationButton);
        cancelAlarmConfigurationButton.setOnClickListener(this);

        Button saveAlarmConfigurationButton = view.findViewById(R.id.saveAlarmConfigurationButton);
        saveAlarmConfigurationButton.setOnClickListener(this);

        TimePicker alarmTimePicker = view.findViewById(R.id.alarmTimePicker);
        alarmTimePicker.setIs24HourView(true);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.saveAlarmConfigurationButton) {

        }

        NavHostFragment.findNavController(this).navigate(R.id.exitAlarmConfigurationAction);
    }
}
