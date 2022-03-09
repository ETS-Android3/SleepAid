package com.example.sleepaid.Fragment.Alarms;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sleepaid.App;
import com.example.sleepaid.Handler.DataHandler;
import com.example.sleepaid.Database.Alarm.Alarm;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Model.SharedViewModel;
import com.example.sleepaid.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@SuppressLint("NewApi")
public class AlarmConfigurationScreenFragment extends Fragment implements View.OnClickListener {
    protected AppDatabase db;

    private SharedViewModel model;

    int[] days;

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
        this.db = AppDatabase.getDatabase(App.getContext());

        this.model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        this.days = new int[]{R.id.monday, R.id.tuesday, R.id.wednesday, R.id.thursday, R.id.friday, R.id.saturday, R.id.sunday};

        Button cancelAlarmConfigurationButton = view.findViewById(R.id.cancelAlarmConfigurationButton);
        cancelAlarmConfigurationButton.setOnClickListener(this);

        Button saveAlarmConfigurationButton = view.findViewById(R.id.saveAlarmConfigurationButton);
        saveAlarmConfigurationButton.setOnClickListener(this);

        this.loadAlarm(model.getAlarmViewType(), model.getSelectedAlarm());
    }

    public void onClick(View view) {
        if (view.getId() == R.id.saveAlarmConfigurationButton) {
            int currentAlarmType = this.model.getAlarmViewType();

            if (model.getSelectedAlarm() == null) {
                Alarm alarm = this.createAlarm(currentAlarmType);

                this.db.alarmDao()
                        .insert(Arrays.asList(alarm))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    alarm.schedule(App.getContext());

                                    List<Alarm> newAlarmList = new ArrayList<>(this.model.getAlarmList(currentAlarmType));
                                    newAlarmList.add(alarm);
                                    Collections.sort(newAlarmList);

                                    this.model.setAlarms(currentAlarmType, newAlarmList);
                                    this.model.setSelectedAlarm(null);

                                    NavHostFragment.findNavController(this).navigate(R.id.exitAlarmConfigurationAction);
                                },
                                Throwable::printStackTrace
                        );
            } else {
                this.updateAlarm(this.model.getSelectedAlarm());

                this.db.alarmDao()
                        .update(Arrays.asList(this.model.getSelectedAlarm()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    List<Alarm> newAlarmList = new ArrayList<>(this.model.getAlarmList(currentAlarmType));
                                    Collections.sort(newAlarmList);

                                    this.model.setAlarms(currentAlarmType, newAlarmList);
                                    this.model.setSelectedAlarm(null);

                                    NavHostFragment.findNavController(this).navigate(R.id.exitAlarmConfigurationAction);
                                },
                                Throwable::printStackTrace
                        );
            }
        } else {
            //TODO add "are you sure" dialog here
            this.model.setSelectedAlarm(null);

            NavHostFragment.findNavController(this).navigate(R.id.exitAlarmConfigurationAction);
        }
    }

    private void loadAlarm(int alarmType, Alarm selectedAlarm) {
        TimePicker alarmTimePicker = getView().findViewById(R.id.alarmTimePicker);
        alarmTimePicker.setIs24HourView(true);

        if (selectedAlarm != null) {
            List<Integer> alarmTimes = DataHandler.getIntsFromString(selectedAlarm.getTime());

            alarmTimePicker.setHour(alarmTimes.get(0));
            alarmTimePicker.setMinute(alarmTimes.get(1));

            for (int i = 0; i < 7; i++) {
                CheckBox day = getView().findViewById(this.days[i]);

                if (selectedAlarm.getDays().charAt(i) == '0') {
                    day.setChecked(false);
                }
            }
        } else {
            this.presetAlarm(alarmType, alarmTimePicker);
        }
    }

    private void presetAlarm(int alarmType, TimePicker alarmTimePicker) {
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

                if (this.model.getGoalMin(goalName) == null) {
                    db.goalDao()
                            .loadAllByNames(new String[]{goalName})
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    goalData -> {
                                        if (!goalData.isEmpty()) {
                                            this.model.setGoal(
                                                    goalName,
                                                    goalData.get(0).getValueMin(),
                                                    goalData.get(0).getValueMax(),
                                                    getResources().getColor(R.color.white),
                                                    getResources().getColor(R.color.white)
                                            );

                                            List<Integer> goalBedtimes = DataHandler.getIntsFromString(this.model.getGoalMin(goalName));

                                            alarmTimePicker.setHour(goalBedtimes.get(0));
                                            alarmTimePicker.setMinute(goalBedtimes.get(1));
                                        }
                                    },
                                    Throwable::printStackTrace
                            );
                } else {
                    List<Integer> goalBedtimes = DataHandler.getIntsFromString(this.model.getGoalMin(goalName));

                    alarmTimePicker.setHour(goalBedtimes.get(0));
                    alarmTimePicker.setMinute(goalBedtimes.get(1));
                }
                break;
        }
    }

    private Alarm createAlarm(int alarmType) {
        return new Alarm(alarmType, this.getName(), this.getTime(), this.getDaysPicked(), "default", 1);
    }

    private void updateAlarm(Alarm alarm) {
        alarm.setName(this.getName());
        alarm.setTime(this.getTime());
        alarm.setDays(this.getDaysPicked());
        alarm.schedule(App.getContext());
        //TODO set sound
    }

    private String getName() {
        EditText name = getView().findViewById(R.id.alarmName);

        return name.getText().toString();
    }

    private String getTime() {
        TimePicker alarmTimePicker = getView().findViewById(R.id.alarmTimePicker);

        String hours = alarmTimePicker.getHour() < 10 ?
                "0" + alarmTimePicker.getHour() :
                Integer.toString(alarmTimePicker.getHour());

        String minutes = alarmTimePicker.getMinute() < 10 ?
                "0" + alarmTimePicker.getMinute() :
                Integer.toString(alarmTimePicker.getMinute());

        return hours + ":" + minutes;
    }

    private String getDaysPicked() {
        String daysPicked = "";

        for (int d : this.days) {
            CheckBox checkbox = getView().findViewById(d);

            daysPicked = checkbox.isChecked() ?
                    daysPicked + "1" :
                    daysPicked + "0";
        }

        return daysPicked;
    }
}
