package com.example.sleepaid.Fragment.Alarms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
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
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@SuppressLint("NewApi")
public class AlarmConfigurationScreenFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
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

        Button selectAlarmSoundButton = view.findViewById(R.id.selectAlarmSoundButton);
        selectAlarmSoundButton.setOnClickListener(this);

        SwitchCompat vibrateButton = view.findViewById(R.id.vibrateButton);
        vibrateButton.setOnCheckedChangeListener(this);

        this.loadAlarm(model.getAlarmViewType(), model.getSelectedAlarm());
    }

    public void onClick(View view) {
        if (view.getId() == R.id.saveAlarmConfigurationButton) {
            int currentAlarmType = this.model.getAlarmViewType();

            if (model.getSelectedAlarm() == null) {
                Alarm alarm = this.createAlarm(currentAlarmType);

                this.db.alarmDao()
                        .insert(Collections.singletonList(alarm))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    alarm.schedule(App.getContext());

                                    List<Alarm> newAlarmList = new ArrayList<>(this.model.getAlarmList(currentAlarmType));
                                    newAlarmList.add(alarm);
                                    Collections.sort(newAlarmList);

                                    this.model.setAlarms(currentAlarmType, newAlarmList);

                                    NavHostFragment.findNavController(this).navigate(R.id.exitAlarmConfigurationAction);
                                },
                                Throwable::printStackTrace
                        );
            } else {
                this.updateAlarm(this.model.getSelectedAlarm());

                this.db.alarmDao()
                        .update(Collections.singletonList(this.model.getSelectedAlarm()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    this.model.getSelectedAlarm().schedule(App.getContext());

                                    List<Alarm> newAlarmList = new ArrayList<>(this.model.getAlarmList(currentAlarmType));
                                    Collections.sort(newAlarmList);

                                    this.model.setAlarms(currentAlarmType, newAlarmList);
                                    this.model.setSelectedAlarm(null);

                                    NavHostFragment.findNavController(this).navigate(R.id.exitAlarmConfigurationAction);
                                },
                                Throwable::printStackTrace
                        );
            }
        } else if (view.getId() == R.id.cancelAlarmConfigurationButton) {
            //TODO add "are you sure" dialog here
            this.model.setSelectedAlarm(null);

            NavHostFragment.findNavController(this).navigate(R.id.exitAlarmConfigurationAction);
        } else if (view.getId() == R.id.selectAlarmSoundButton) {
            NavHostFragment.findNavController(this).navigate(R.id.selectAlarmSoundAction);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(100);
        }
    }

    private void loadAlarm(int alarmType, Alarm selectedAlarm) {
        TimePicker alarmTimePicker = getView().findViewById(R.id.alarmTimePicker);
        alarmTimePicker.setIs24HourView(true);

        if (selectedAlarm != null) {
            List<Integer> alarmTimes = DataHandler.getIntsFromString(selectedAlarm.getTime());

            alarmTimePicker.setHour(alarmTimes.get(0));
            alarmTimePicker.setMinute(alarmTimes.get(1));

            EditText alarmName = getView().findViewById(R.id.alarmName);
            alarmName.setText(selectedAlarm.getName() == null ? "" : selectedAlarm.getName());

            for (int i = 0; i < 7; i++) {
                CheckBox day = getView().findViewById(this.days[i]);

                if (selectedAlarm.getDays().charAt(i) == '0') {
                    day.setChecked(false);
                }
            }

            TextView alarmSound = getView().findViewById(R.id.soundName);
            alarmSound.setText(this.model.getSelectedSound());

            SwitchCompat vibrateButton = getView().findViewById(R.id.vibrateButton);
            vibrateButton.setChecked(selectedAlarm.getVibrate() == 1);
        } else {
            this.presetAlarm(alarmType, alarmTimePicker);
        }
    }

    private void presetAlarm(int alarmType, TimePicker alarmTimePicker) {
        if (alarmType == 2) {
            //"nap"
            alarmTimePicker.setHour(12);
            alarmTimePicker.setMinute(0);
        } else {
            //"morning" or "bedtime"
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
        }
    }

    private Alarm createAlarm(int alarmType) {
        return new Alarm(
                alarmType,
                this.getName(),
                this.getTime(),
                this.getDaysPicked(),
                this.getSound(),
                this.getVibrate(),
                1
        );
    }

    private void updateAlarm(Alarm alarm) {
        alarm.setName(this.getName());
        alarm.setTime(this.getTime());
        alarm.setDays(this.getDaysPicked());
        alarm.setSound(this.getSound());
        alarm.setVibrate(this.getVibrate());
        alarm.setIsOn(1);
    }

    private String getName() {
        EditText name = getView().findViewById(R.id.alarmName);

        return name.getText().toString();
    }

    private String getTime() {
        TimePicker alarmTimePicker = getView().findViewById(R.id.alarmTimePicker);

        return DataHandler.getFormattedTime(alarmTimePicker.getHour(), alarmTimePicker.getMinute());
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

    private String getSound() {
        return this.model.getSelectedSound();
    }

    private int getVibrate() {
        SwitchCompat vibrateButton = getView().findViewById(R.id.vibrateButton);

        return vibrateButton.isChecked() ? 1 : 0;
    }
}
