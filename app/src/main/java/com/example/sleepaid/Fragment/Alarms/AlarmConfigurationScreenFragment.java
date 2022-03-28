package com.example.sleepaid.Fragment.Alarms;

import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioAttributes;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sleepaid.App;
import com.example.sleepaid.Component.Modal;
import com.example.sleepaid.Database.Alarm.Alarm;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Handler.DataHandler;
import com.example.sleepaid.Model.SharedViewModel;
import com.example.sleepaid.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class AlarmConfigurationScreenFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private View view;

    protected AppDatabase db;

    private SharedViewModel model;

    int[] days;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Alarm newAlarm = createAlarm(model.getAlarmViewType());

                if (model.getSelectedAlarm() != null) {
                    if (!newAlarm.equals(model.getSelectedAlarm())) {
                        exitAlarmConfiguration();
                    } else {
                        cancelAlarmChanges();
                    }
                } else {
                    exitAlarmConfiguration();
                }
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alarm_configuration_screen, container, false);
    }

    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        this.view = view;

        this.db = AppDatabase.getDatabase(App.getContext());

        this.model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        this.days = new int[]{R.id.monday, R.id.tuesday, R.id.wednesday, R.id.thursday, R.id.friday, R.id.saturday, R.id.sunday};

        Button cancelAlarmConfigurationButton = this.view.findViewById(R.id.cancelAlarmConfigurationButton);
        cancelAlarmConfigurationButton.setOnClickListener(this);

        Button saveAlarmConfigurationButton = this.view.findViewById(R.id.saveAlarmConfigurationButton);
        saveAlarmConfigurationButton.setOnClickListener(this);

        Button selectAlarmSoundButton = this.view.findViewById(R.id.selectAlarmSoundButton);
        selectAlarmSoundButton.setOnClickListener(this);

        SwitchCompat vibrateButton = this.view.findViewById(R.id.vibrateButton);
        vibrateButton.setOnCheckedChangeListener(this);

        this.loadAlarm(model.getAlarmViewType(), model.getSelectedConfiguration());
    }

    private void exitAlarmConfiguration() {
        DialogInterface.OnClickListener discardAction = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                cancelAlarmChanges();
            }
        };

        DialogInterface.OnClickListener saveAction = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                saveAlarmChanges();
            }
        };

        Modal.show(
                requireActivity(),
                getString(R.string.exit_alarm_configuration),
                getString(R.string.save_modal),
                saveAction,
                getString(R.string.discard_modal),
                discardAction
        );
    }

    private void cancelAlarmChanges() {
        this.model.setSelectedAlarm(null);
        this.model.setSelectedConfiguration(null);

        NavHostFragment.findNavController(this).navigate(R.id.exitAlarmConfigurationAction);
    }

    private void saveAlarmChanges() {
        int currentAlarmType = this.model.getAlarmViewType();

        if (model.getSelectedAlarm() == null) {
            Alarm alarm = this.createAlarm(currentAlarmType);

            this.db.alarmDao()
                    .insert(Collections.singletonList(alarm))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            alarmId -> {
                                alarm.setId(alarmId.get(0).intValue());
                                alarm.schedule(App.getContext());
                                Toast.makeText(getActivity(), "Alarm scheduled successfully!", Toast.LENGTH_SHORT).show();

                                List<Alarm> newAlarmList = new ArrayList<>(this.model.getAlarmList(currentAlarmType));
                                newAlarmList.add(alarm);
                                Collections.sort(newAlarmList);

                                this.model.setAlarms(currentAlarmType, newAlarmList);
                                this.model.setSelectedAlarm(null);
                                this.model.setSelectedConfiguration(null);

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
                                Toast.makeText(getActivity(), "Alarm scheduled successfully!", Toast.LENGTH_SHORT).show();

                                List<Alarm> newAlarmList = new ArrayList<>(this.model.getAlarmList(currentAlarmType));
                                Collections.sort(newAlarmList);

                                this.model.setAlarms(currentAlarmType, newAlarmList);
                                this.model.setSelectedAlarm(null);
                                this.model.setSelectedConfiguration(null);

                                NavHostFragment.findNavController(this).navigate(R.id.exitAlarmConfigurationAction);
                            },
                            Throwable::printStackTrace
                    );
        }
    }

    public void onClick(View view) {
        if (view.getId() == R.id.saveAlarmConfigurationButton) {
            this.saveAlarmChanges();
        } else if (view.getId() == R.id.cancelAlarmConfigurationButton) {
            this.cancelAlarmChanges();
        } else if (view.getId() == R.id.selectAlarmSoundButton) {
            this.model.setSelectedConfiguration(this.createAlarm(this.model.getAlarmViewType()));

            NavHostFragment.findNavController(this).navigate(R.id.selectAlarmSoundAction);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            Vibrator vibrator = (Vibrator) requireActivity().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(
                    VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE),
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .build()
            );
        }
    }

    private void loadAlarm(int alarmType, Alarm selectedAlarm) {
        TimePicker alarmTimePicker = this.view.findViewById(R.id.alarmTimePicker);
        alarmTimePicker.setIs24HourView(true);

        if (selectedAlarm != null) {
            List<Integer> alarmTimes = DataHandler.getIntsFromString(selectedAlarm.getTime());

            alarmTimePicker.setHour(alarmTimes.get(0));
            alarmTimePicker.setMinute(alarmTimes.get(1));

            EditText alarmName = this.view.findViewById(R.id.alarmName);
            alarmName.setText(selectedAlarm.getName());

            for (int i = 0; i < 7; i++) {
                CheckBox day = this.view.findViewById(this.days[i]);

                if (selectedAlarm.getDays().charAt(i) == '0') {
                    day.setChecked(false);
                }
            }

            TextView alarmSound = this.view.findViewById(R.id.soundName);
            alarmSound.setText(selectedAlarm.getSound());

            SwitchCompat vibrateButton = this.view.findViewById(R.id.vibrateButton);
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

            this.model.setSelectedConfiguration(new Alarm(
                            alarmType,
                            "",
                            "12:00",
                            "1111111",
                            "Default",
                            1,
                            1
                    ));
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
                                                ContextCompat.getColor(App.getContext(), R.color.white),
                                                ContextCompat.getColor(App.getContext(), R.color.white)
                                        );

                                        List<Integer> goals = DataHandler.getIntsFromString(this.model.getGoalMin(goalName));

                                        alarmTimePicker.setHour(goals.get(0));
                                        alarmTimePicker.setMinute(goals.get(1));

                                        this.model.setSelectedConfiguration(new Alarm(
                                                alarmType,
                                                "",
                                                DataHandler.getFormattedTime(goals.get(0), goals.get(1)),
                                                "1111111",
                                                "Default",
                                                1,
                                                1
                                        ));
                                    }
                                },
                                Throwable::printStackTrace
                        );
            } else {
                List<Integer> goals = DataHandler.getIntsFromString(this.model.getGoalMin(goalName));

                alarmTimePicker.setHour(goals.get(0));
                alarmTimePicker.setMinute(goals.get(1));

                this.model.setSelectedConfiguration(new Alarm(
                        alarmType,
                        "",
                        DataHandler.getFormattedTime(goals.get(0), goals.get(1)),
                        "1111111",
                        "Default",
                        1,
                        1
                ));
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
        EditText name = this.view.findViewById(R.id.alarmName);

        return name.getText().toString();
    }

    private String getTime() {
        TimePicker alarmTimePicker = this.view.findViewById(R.id.alarmTimePicker);

        return DataHandler.getFormattedTime(alarmTimePicker.getHour(), alarmTimePicker.getMinute());
    }

    private String getDaysPicked() {
        String daysPicked = "";

        for (int d : this.days) {
            CheckBox checkbox = this.view.findViewById(d);

            daysPicked = checkbox.isChecked() ?
                    daysPicked + "1" :
                    daysPicked + "0";
        }

        return daysPicked;
    }

    private String getSound() {
        return this.model.getSelectedConfiguration().getSound();
    }

    private int getVibrate() {
        SwitchCompat vibrateButton = this.view.findViewById(R.id.vibrateButton);

        return vibrateButton.isChecked() ? 1 : 0;
    }
}
