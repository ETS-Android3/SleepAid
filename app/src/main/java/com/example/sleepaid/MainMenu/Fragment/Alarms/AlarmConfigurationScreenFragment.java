package com.example.sleepaid.MainMenu.Fragment.Alarms;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sleepaid.App;
import com.example.sleepaid.Database.Alarm.Alarm;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Model.SharedViewModel;
import com.example.sleepaid.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@SuppressLint("NewApi")
public class AlarmConfigurationScreenFragment extends Fragment implements View.OnClickListener {
    protected AppDatabase db;

    private SharedViewModel model;

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

        model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        Button cancelAlarmConfigurationButton = view.findViewById(R.id.cancelAlarmConfigurationButton);
        cancelAlarmConfigurationButton.setOnClickListener(this);

        Button saveAlarmConfigurationButton = view.findViewById(R.id.saveAlarmConfigurationButton);
        saveAlarmConfigurationButton.setOnClickListener(this);

        TimePicker alarmTimePicker = view.findViewById(R.id.alarmTimePicker);
        alarmTimePicker.setIs24HourView(true);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.saveAlarmConfigurationButton) {
            int currentAlarmType = model.getAlarmViewType();

            int[] days = {R.id.monday, R.id.tuesday, R.id.wednesday, R.id.thursday, R.id.friday, R.id.saturday, R.id.sunday};
            String daysPicked = "";

            for (int d : days) {
                CheckBox checkbox = getView().findViewById(d);

                daysPicked = checkbox.isChecked() ?
                        daysPicked + "1" :
                        daysPicked + "0";
            }

            TimePicker alarmTimePicker = getView().findViewById(R.id.alarmTimePicker);

            String hours = alarmTimePicker.getHour() < 10 ?
                    "0" + alarmTimePicker.getHour() :
                    Integer.toString(alarmTimePicker.getHour());

            String minutes = alarmTimePicker.getMinute() < 10 ?
                    "0" + alarmTimePicker.getMinute() :
                    Integer.toString(alarmTimePicker.getMinute());

            String time = hours + ":" + minutes;

            Alarm alarm = new Alarm(currentAlarmType, time, daysPicked, "default");

            db.alarmDao()
                    .insert(Arrays.asList(alarm))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            () -> {
                                List<Alarm> newAlarmList = new ArrayList<>(model.getAlarmList(currentAlarmType));
                                newAlarmList.add(alarm);
                                Collections.sort(newAlarmList);

                                model.setAlarms(currentAlarmType, newAlarmList);

                                NavHostFragment.findNavController(this).navigate(R.id.exitAlarmConfigurationAction);
                            },
                            Throwable::printStackTrace
                    );
        } else {
            NavHostFragment.findNavController(this).navigate(R.id.exitAlarmConfigurationAction);
        }
    }
}
