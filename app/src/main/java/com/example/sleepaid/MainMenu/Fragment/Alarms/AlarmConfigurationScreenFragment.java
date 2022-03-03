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
import com.example.sleepaid.DataHandler;
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

    //TODO override back button with "are you sure" dialog
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        db = AppDatabase.getDatabase(App.getContext());

        model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        Button cancelAlarmConfigurationButton = view.findViewById(R.id.cancelAlarmConfigurationButton);
        cancelAlarmConfigurationButton.setOnClickListener(this);

        Button saveAlarmConfigurationButton = view.findViewById(R.id.saveAlarmConfigurationButton);
        saveAlarmConfigurationButton.setOnClickListener(this);

        this.presetAlarmTime(model.getAlarmViewType());
    }

    public void onClick(View view) {
        if (view.getId() == R.id.saveAlarmConfigurationButton) {
            int currentAlarmType = model.getAlarmViewType();

            Alarm alarm = this.createAlarm(currentAlarmType);

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
            //TODO add "are you sure" dialog here
            NavHostFragment.findNavController(this).navigate(R.id.exitAlarmConfigurationAction);
        }
    }

    private void presetAlarmTime(int alarmType) {
        TimePicker alarmTimePicker = getView().findViewById(R.id.alarmTimePicker);
        alarmTimePicker.setIs24HourView(true);

        switch (alarmType) {
            //"nap"
            case 2:
                alarmTimePicker.setHour(12);
                alarmTimePicker.setMinute(0);
                break;

            //"morning" or "bedtime"
            default:
                String goalName = alarmType == 1 ?
                        "Wake-up time" :
                        "Bedtime";

                if (model.getGoalMin(goalName) == null) {
                    db.goalDao()
                            .loadAllByNames(new String[]{goalName})
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    goalData -> {
                                        if (!goalData.isEmpty()) {
                                            model.setGoal(
                                                    goalName,
                                                    goalData.get(0).getValueMin(),
                                                    goalData.get(0).getValueMax(),
                                                    getResources().getColor(R.color.white),
                                                    getResources().getColor(R.color.white)
                                            );

                                            List<Integer> goalBedtimes = DataHandler.getIntsFromString(model.getGoalMin(goalName));

                                            alarmTimePicker.setHour(goalBedtimes.get(0));
                                            alarmTimePicker.setMinute(goalBedtimes.get(1));
                                        }
                                    },
                                    Throwable::printStackTrace
                            );
                } else {
                    List<Integer> goalBedtimes = DataHandler.getIntsFromString(model.getGoalMin(goalName));

                    alarmTimePicker.setHour(goalBedtimes.get(0));
                    alarmTimePicker.setMinute(goalBedtimes.get(1));
                }
                break;
        }
    }

    private Alarm createAlarm(int alarmType) {
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

        return new Alarm(alarmType, time, daysPicked, "default");
    }
}
