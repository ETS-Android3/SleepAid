package com.example.sleepaid.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sleepaid.App;
import com.example.sleepaid.Component.Modal;
import com.example.sleepaid.Database.Alarm.Alarm;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Database.Configuration.Configuration;
import com.example.sleepaid.Database.Goal.Goal;
import com.example.sleepaid.Database.Notification.Notification;
import com.example.sleepaid.Handler.DataHandler;
import com.example.sleepaid.Model.SharedViewModel;
import com.example.sleepaid.R;
import com.example.sleepaid.Service.AlarmAndNotificationService;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SettingsAdapter extends BaseAdapter {
    private Context context;
    private SharedViewModel model;
    private AppDatabase db;

    private List<String> names;
    private List<String> values;

    private String time;

    public SettingsAdapter(Context context,
                           SharedViewModel model,
                           List<String> names,
                           List<String> values) {
        this.context = context;
        this.model = model;
        this.db = AppDatabase.getDatabase(App.getContext());

        this.names = names;
        this.values = values;
    }

    public int getCount() {
        return names.size();
    }

    public Object getItem(int arg) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View row, ViewGroup parent) {
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) App.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.settings_row, parent, false);
        }

        TextView namesText = row.findViewById(R.id.name);
        EditText valuesText = row.findViewById(R.id.value);
        Button editButton = row.findViewById(R.id.editButton);
        Button doneButton = row.findViewById(R.id.doneButton);

        namesText.setText(names.get(position));
        valuesText.setText(values.get(position));

        valuesText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (!names.get(position).equals("Allow naps")) {
                    if ((valuesText.getText().length() == 2 && valuesText.getText().toString().matches("\\d\\d")) ||
                            (valuesText.getText().length() > 5 && valuesText.getText().length() <= 8 && valuesText.getText().toString().matches("\\d\\d[^a-z]*\\d\\d"))) {
                        valuesText.setText(valuesText.getText() + ":");
                        valuesText.setSelection(valuesText.length());
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });

        editButton.setOnClickListener(view -> {
            editButton.setVisibility(View.INVISIBLE);
            doneButton.setVisibility(View.VISIBLE);

            valuesText.setEnabled(true);
            valuesText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            valuesText.requestFocus();
        });

        doneButton.setOnClickListener(view -> {
            if (names.get(position).equals("Allow naps")) {
                if (!valuesText.getText().toString().contains(".")) {
                    valuesText.setText(valuesText.getText() + ".");
                }
            } else {
                if (!valuesText.getText().toString().contains(" - ")) {
                    if (valuesText.getText().toString().contains("-")) {
                        valuesText.setText(valuesText.getText().toString().replace("-", " - "));
                    }
                }
            }

            if (validateValue(valuesText.getText().toString(), names.get(position))) {
                if (!valuesText.getText().toString().equals(values.get(position))) {
                    DialogInterface.OnClickListener yesAction = (dialog, whichButton) -> {
                        editButton.setVisibility(View.VISIBLE);
                        doneButton.setVisibility(View.GONE);

                        valuesText.setEnabled(false);

                        saveSetting(valuesText.getText().toString(), names.get(position));
                    };

                    DialogInterface.OnClickListener cancelAction = (dialog, whichButton) -> {
                        editButton.setVisibility(View.VISIBLE);
                        doneButton.setVisibility(View.GONE);

                        valuesText.setEnabled(false);
                        valuesText.setText(values.get(position));
                    };

                    Modal.show(
                            context,
                            this.context.getString(R.string.save_setting),
                            this.context.getString(R.string.yes_modal),
                            yesAction,
                            this.context.getString(R.string.cancel_modal),
                            cancelAction
                    );
                } else {
                    editButton.setVisibility(View.VISIBLE);
                    doneButton.setVisibility(View.GONE);

                    valuesText.setEnabled(false);
                }
            }
        });

        return row;
    }

    private boolean validateValue(String value, String setting) {
        if (setting.equals("Allow naps")) {
            if (value.equals("Yes.") || value.equals("No.")) {
                return true;
            }

            DialogInterface.OnClickListener okAction = (dialog, whichButton) -> {};

            Modal.show(
                    this.context,
                    this.context.getString(R.string.nap_setting_validation),
                    this.context.getString(R.string.ok_modal),
                    okAction,
                    null,
                    null
            );

            return false;
        } else {
            if (value.matches("\\d\\d:\\d\\d - \\d\\d:\\d\\d")) {
                return true;
            }

            DialogInterface.OnClickListener okAction = (dialog, whichButton) -> {};

            Modal.show(
                    this.context,
                    this.context.getString(R.string.goal_setting_validation),
                    this.context.getString(R.string.ok_modal),
                    okAction,
                    null,
                    null
            );

            return false;
        }
    }

    private void saveSetting(String value, String setting) {
        if (setting.equals("Allow naps")) {
            Configuration napConfiguration = new Configuration("supportNaps", value);

            this.db.configurationDao()
                    .update(napConfiguration)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            () -> {
                                if (value.equals("No.")) {
                                    this.cancelAlarms(null, 2);
                                } else {
                                    this.loadNapTime();
                                }
                            },
                            Throwable::printStackTrace
                    );
        } else {
            this.db.goalDao()
                    .getAll()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            goalData -> {
                                Goal settingGoal = goalData.stream()
                                        .filter(g -> g.getName().equals(setting))
                                        .findFirst()
                                        .get();
                                settingGoal.setValueMin(value.substring(0, 5));
                                settingGoal.setValueMax(value.substring(8));

                                Goal otherGoal = goalData.stream()
                                        .filter(g -> !g.getName().equals(setting) &&
                                                !g.getName().equals("Sleep duration"))
                                        .findFirst()
                                        .get();

                                List<Integer> wakeupTimes = settingGoal.getName().equals("Wake-up time") ?
                                        DataHandler.getIntsFromString(settingGoal.getValueMin()) :
                                        DataHandler.getIntsFromString(otherGoal.getValueMin());

                                List<Integer> bedtimes = settingGoal.getName().equals("Bedtime") ?
                                        DataHandler.getIntsFromString(settingGoal.getValueMin()) :
                                        DataHandler.getIntsFromString(otherGoal.getValueMin());

                                ZonedDateTime wakeupTime = ZonedDateTime.now()
                                        .withHour(wakeupTimes.get(0))
                                        .withMinute(wakeupTimes.get(1))
                                        .plusDays(1);

                                ZonedDateTime bedtime = ZonedDateTime.now()
                                        .withHour(bedtimes.get(0))
                                        .withMinute(bedtimes.get(1));

                                List<Integer> differenceTimes = DataHandler.getIntsFromString(Duration.between(wakeupTime, bedtime).toString());

                                int hours = differenceTimes.size() >= 1 ?
                                        differenceTimes.get(0) :
                                        0;
                                int minutes = differenceTimes.size() >= 2 ?
                                        differenceTimes.get(1) :
                                        0;
                                String duration = DataHandler.getFormattedDuration(hours, minutes);

                                Goal durationGoal = goalData.stream()
                                        .filter(g -> g.getName().equals("Sleep duration"))
                                        .findFirst()
                                        .get();
                                durationGoal.setValueMin(duration);
                                durationGoal.setValueMax(duration);

                                this.updateGoals(new ArrayList<>(Arrays.asList(settingGoal, durationGoal)));
                            },
                            Throwable::printStackTrace
                    );
        }
    }

    private void loadNapTime() {
        this.db.answerDao()
                .loadValuesByQuestionIds(new int[]{25})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        answerData -> {
                            if (!answerData.get(0).isEmpty() && !answerData.get(0).equals("0")) {
                                this.saveAlarms(AlarmAndNotificationService.createAlarms(2, answerData.get(0)));
                            }
                        },
                        Throwable::printStackTrace
                );
    }

    private void updateGoals(List<Goal> goalList) {
        this.db.goalDao()
                .update(goalList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> cancelAlarms(goalList.get(0), goalList.get(0).getName().equals("Bedtime") ? 3 : 1),
                        Throwable::printStackTrace
                );
    }

    private void cancelAlarms(Goal goal, int alarmType) {
        this.db.alarmDao()
                .loadAllByTypes(new int[]{alarmType})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        alarmData -> {
                            for (Alarm a : alarmData) {
                                if (a.getIsOn() == 1) {
                                    a.cancel(this.context);
                                }
                            }

                            if (goal != null) {
                                this.time = goal.getValueMin();
                            }

                            this.deleteAlarms(alarmData, goal.getName(), alarmType);
                        },
                        Throwable::printStackTrace
                );
    }

    private void deleteAlarms(List<Alarm> alarmList, String goalName, int alarmType) {
        this.db.alarmDao()
                .delete(alarmList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            if (alarmType != 2) {
                                this.model.setGoalModel(goalName, null);
                                this.model.setGoalModel("Sleep duration", null);

                                this.saveAlarms(AlarmAndNotificationService.createAlarms(alarmType, this.time));
                            }

                            this.model.setAlarmListModel(alarmType, null);
                        },
                        Throwable::printStackTrace
                );
    }

    private void saveAlarms(List<Alarm> alarmList) {
        db.alarmDao()
                .insert(alarmList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        alarmIds -> {
                            for (int i = 0; i < alarmIds.size(); i++) {
                                alarmList.get(i).setId(alarmIds.get(i).intValue());
                                alarmList.get(i).schedule(this.context);
                            }

                            this.cancelNotifications(alarmList.get(0).getType());
                        },
                        Throwable::printStackTrace
                );
    }

    private void cancelNotifications(int type) {
        String[] notificationNames = new String[]{};

        if (type == 1) {
            notificationNames = new String[]{
                    "It's time to fill in your morning sleep diary!",
                    "You still haven't filled in your morning sleep diary. You can do it until 00:00."
            };
        } else if (type == 3) {
            notificationNames = new String[]{
                    "It's almost bedtime!",
                    "It's time to fill in your bedtime sleep diary!",
                    "You still haven't filled in your bedtime sleep diary. You can do it until 12:00."
            };
        }

        this.db.notificationDao()
                .loadAllByNames(notificationNames)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        notificationData -> {
                            for (Notification n : notificationData) {
                                n.cancel(this.context);
                            }

                            this.deleteNotifications(notificationData, type);
                        },
                        Throwable::printStackTrace
                );
    }

    private void deleteNotifications(List<Notification> notificationList, int type) {
        this.db.notificationDao()
                .delete(notificationList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> this.saveNotifications(AlarmAndNotificationService.createNotifications(type, this.time)),
                        Throwable::printStackTrace
                );
    }
    
    private void saveNotifications(List<Notification> notificationList) {
        db.notificationDao()
                .insert(notificationList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        notificationIds -> {
                            for (int i = 0; i < notificationIds.size(); i++) {
                                notificationList.get(i).setId(notificationIds.get(i).intValue());
                                notificationList.get(i).schedule(this.context);
                            }
                        },
                        Throwable::printStackTrace
                );
    }
}
