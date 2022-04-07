package com.example.sleepaid.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sleepaid.App;
import com.example.sleepaid.Component.Modal;
import com.example.sleepaid.Database.Alarm.Alarm;
import com.example.sleepaid.Database.Answer.Answer;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Database.Configuration.Configuration;
import com.example.sleepaid.Database.Goal.Goal;
import com.example.sleepaid.Database.Notification.Notification;
import com.example.sleepaid.Handler.DataHandler;
import com.example.sleepaid.R;
import com.example.sleepaid.Service.BlueLightFilter.BlueLightFilterBroadcastReceiverService;
import com.example.sleepaid.Service.BlueLightFilter.BlueLightFilterService;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class InitialSettingsService {
    Fragment fragment;
    AppDatabase db;

    List<Configuration> configurationList;
    List<Goal> goalList;
    List<Alarm> alarmList;
    List<Notification> notificationList;

    public InitialSettingsService(Fragment fragment, AppDatabase db) {
        this.fragment = fragment;
        this.db = db;

        this.configurationList = new ArrayList<>();
        this.goalList = new ArrayList<>();
        this.alarmList = new ArrayList<>();
        this.notificationList = new ArrayList<>();
    }

    public void getSettings() {
        getConfigurationList();
    }

    private void createSettings() {
        createConfigurations();
    }

    private void getConfigurationList() {
        db.answerDao()
                .loadValuesByQuestionIds(new int[]{24})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        answerData -> {
                            Configuration c = new Configuration("supportNaps", answerData.get(0));
                            configurationList.add(c);

                            getGoalList();
                        },
                        Throwable::printStackTrace
                );
    }

    private void createConfigurations() {
        db.configurationDao()
                .insert(configurationList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> createGoals(),
                        Throwable::printStackTrace
                );
    }

    private void getGoalList() {
        db.answerDao()
                .loadValuesByQuestionIds(new int[]{22, 23})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        answerData -> {
                            List<String> bedtimes = DataHandler.getGoalsFromString(answerData.get(0));
                            Goal bedtime = new Goal("Bedtime", bedtimes.get(0), bedtimes.get(1));
                            goalList.add(bedtime);

                            List<String> wakeupTimes = DataHandler.getGoalsFromString(answerData.get(1));
                            Goal wakeupTime = new Goal("Wake-up time", wakeupTimes.get(0), wakeupTimes.get(1));
                            goalList.add(wakeupTime);

                            int minBedHour = DataHandler.getIntsFromString(bedtimes.get(0)).get(0);
                            int minWakeupHour = DataHandler.getIntsFromString(wakeupTimes.get(0)).get(0);

                            int duration = minWakeupHour - minBedHour <= 0 ?
                                    24 + (minWakeupHour - minBedHour) :
                                    minWakeupHour - minBedHour;
                            Goal sleepDuration = new Goal("Sleep duration", duration + "h", duration + "h");
                            goalList.add(sleepDuration);

                            getAlarmList(wakeupTimes.get(0), bedtimes.get(0));
                        },
                        Throwable::printStackTrace
                );
    }

    private void createGoals() {
        db.goalDao()
                .insert(goalList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> createAlarms(),
                        Throwable::printStackTrace
                );
    }

    private void getAlarmList(String wakeupTime, String bedtime) {
        this.alarmList = AlarmAndNotificationService.createAlarms(1, wakeupTime);
        this.alarmList.addAll(AlarmAndNotificationService.createAlarms(3, bedtime));

        if (configurationList.get(0).getValue().equals("Yes.")) {
            db.answerDao()
                    .loadValuesByQuestionIds(new int[]{25})
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            answerData -> {
                                this.alarmList.addAll(AlarmAndNotificationService.createAlarms(2, answerData.get(0)));

                                getNotificationList(wakeupTime, bedtime);
                            },
                            Throwable::printStackTrace
                    );
        }
        else {
            getNotificationList(wakeupTime, bedtime);
        }
    }

    private void createAlarms() {
        db.alarmDao()
                .insert(alarmList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        alarmIds -> {
                            for (int i = 0; i < alarmIds.size(); i++) {
                                alarmList.get(i).setId(alarmIds.get(i).intValue());
                            }

                            createNotifications();
                        },
                        Throwable::printStackTrace
                );
    }

    private void getNotificationList(String wakeupTime, String bedtime) {
        this.notificationList = AlarmAndNotificationService.createNotifications(1, wakeupTime);
        this.notificationList.addAll(AlarmAndNotificationService.createNotifications(3, bedtime));

        this.scoreSHAPS();
    }

    private void scoreSHAPS() {
        AtomicInteger sleepHygieneScore = new AtomicInteger();
        AtomicInteger caffeineAnswered = new AtomicInteger();
        AtomicInteger caffeineScore = new AtomicInteger();

        List<Integer> section1 = Arrays.asList(1, 2, 3, 4, 5, 6, 9, 13);
        List<Integer> section2 = Arrays.asList(1, 4, 7, 8, 16, 18);

        this.db.answerDao()
                .loadAllByQuestionnaireIds(new int[]{2})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        answerData -> {
                            for (Answer a : answerData) {
                                if (a.getQuestionId() == 7) {
                                    if (section1.contains(a.getSection())) {
                                        if (a.getValue().contains("disruptive")) {
                                            sleepHygieneScore.set(sleepHygieneScore.get() + 3);
                                        } else {
                                            sleepHygieneScore.set(sleepHygieneScore.get() + 1);
                                        }
                                    } else {
                                        if (a.getValue().contains("beneficial")) {
                                            sleepHygieneScore.set(sleepHygieneScore.get() + 1);
                                        } else {
                                            sleepHygieneScore.set(sleepHygieneScore.get() + 3);
                                        }
                                    }
                                } else {
                                    if (section2.contains(a.getSection())) {
                                        if (a.getValue().contains("Yes")) {
                                            caffeineAnswered.getAndIncrement();
                                        } else if (a.getValue().contains("No")){
                                            caffeineAnswered.getAndIncrement();
                                            caffeineScore.getAndIncrement();
                                        }
                                    } else {
                                        if (a.getValue().contains("No")) {
                                            caffeineAnswered.getAndIncrement();
                                        } else if (a.getValue().contains("Yes")){
                                            caffeineAnswered.getAndIncrement();
                                            caffeineScore.getAndIncrement();
                                        }
                                    }
                                }
                            }

                            caffeineScore.set(caffeineScore.get() / caffeineAnswered.get() * 100);

                            createInformationNotifications(sleepHygieneScore.get(), caffeineScore.get());
                        },
                        Throwable::printStackTrace
                );
    }

    private void createInformationNotifications(int sleepHygieneScore, int caffeineScore) {
        int frequency;

        if (sleepHygieneScore <= 21 && caffeineScore >= 66) {
            frequency = 5;
        } else if (sleepHygieneScore <= 29 && caffeineScore >= 33) {
            frequency = 3;
        } else {
            frequency = 1;
        }

        Notification dinnerNotification = new Notification(
                "Don't leave your dinner too late",
                App.getContext().getString(R.string.dinner_notification),
                "18:00",
                frequency,
                0
        );
        this.notificationList.add(dinnerNotification);

        Notification exerciseNotification = new Notification(
                "Physical activity can improve your sleep quality",
                App.getContext().getString(R.string.exercise_notification),
                "13:00",
                frequency,
                0
        );
        this.notificationList.add(exerciseNotification);

        Notification caffeineNotification = new Notification(
                "Having caffeine in the evening may disrupt your sleep",
                App.getContext().getString(R.string.caffeine_notification),
                "11:00",
                frequency,
                0
        );
        this.notificationList.add(caffeineNotification);

        createSettings();
    }

    private void createNotifications() {
        db.notificationDao()
                .insert(notificationList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        notificationIds -> {
                            for (Alarm a : alarmList) {
                                a.schedule(this.fragment.requireActivity());
                            }

                            for (int i = 0; i < notificationIds.size(); i++) {
                                notificationList.get(i).setId(notificationIds.get(i).intValue());
                                notificationList.get(i).schedule(this.fragment.requireActivity());
                            }

                            this.setupBlueLightFilter();

                            Intent intent = new Intent(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            NavHostFragment.findNavController(this.fragment).navigate(R.id.finishQuestionnairesAction);
                        },
                        Throwable::printStackTrace
                );
    }

    private void setupBlueLightFilter() {
        if (!Settings.canDrawOverlays(App.getContext())) {
            SharedPreferences sharedPref = this.fragment.getActivity().getPreferences(Context.MODE_PRIVATE);
            boolean haveAskedForPermission = sharedPref.getBoolean("asked_blue_light_filter_permission", false);

            if (!haveAskedForPermission) {
                sharedPref = this.fragment.getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("asked_blue_light_filter_permission", true);
                editor.apply();

                DialogInterface.OnClickListener yesAction = (dialog, whichButton) -> {
                    Intent overlayPermissionIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    this.fragment.getActivity().startActivity(overlayPermissionIntent);
                };

                DialogInterface.OnClickListener noAction = (dialog, whichButton) -> {
                };

                Modal.show(
                        this.fragment.requireActivity(),
                        App.getContext().getString(R.string.blue_light_filter_permission),
                        App.getContext().getString(R.string.yes_modal),
                        yesAction,
                        App.getContext().getString(R.string.no_modal),
                        noAction
                );
            }
        } else {
            AlarmManager alarmManager = (AlarmManager) App.getContext().getSystemService(Context.ALARM_SERVICE);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()) {
                Intent startIntent = new Intent(App.getContext(), BlueLightFilterBroadcastReceiverService.class);

                long startTime = ZonedDateTime.now()
                        .withHour(20)
                        .withMinute(0)
                        .toInstant()
                        .toEpochMilli();

                startIntent.putExtra("HOUR", 20);
                startIntent.putExtra("MINUTE", 0);

                PendingIntent startPendingIntent = PendingIntent.getBroadcast(App.getContext(), (int) startTime, startIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

                alarmManager.setAlarmClock(
                        new AlarmManager.AlarmClockInfo(
                                Math.max(startTime, System.currentTimeMillis()),
                                startPendingIntent
                        ),
                        startPendingIntent
                );
            }
        }
    }
}
