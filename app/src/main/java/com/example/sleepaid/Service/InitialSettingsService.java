package com.example.sleepaid.Service;

import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sleepaid.App;
import com.example.sleepaid.Database.Alarm.Alarm;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Database.Configuration.Configuration;
import com.example.sleepaid.Database.Goal.Goal;
import com.example.sleepaid.Database.Notification.Notification;
import com.example.sleepaid.Handler.DataHandler;
import com.example.sleepaid.R;

import java.util.ArrayList;
import java.util.List;

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
        Configuration notificationSound = new Configuration("notificationSound", "Default");
        configurationList.add(notificationSound);

        Configuration notificationVibration = new Configuration("notificationVibrate", "1");
        configurationList.add(notificationVibration);

        db.answerDao()
                .loadValuesByQuestionIds(new int[]{3})
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
                .loadValuesByQuestionIds(new int[]{1, 2})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        answerData -> {
                            //List<Integer> bedTimes = DataHandler.getIntsFromString(answerData.get(0));
                            List<String> bedtimes = DataHandler.getGoalsFromString(answerData.get(0));
                            Goal bedtime = new Goal("Bedtime", bedtimes.get(0), bedtimes.get(1));
                            goalList.add(bedtime);

                            //List<Integer> wakeupTimes = DataHandler.getIntsFromString(answerData.get(1));
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

                            getAlarmList(minWakeupHour, minBedHour);
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

    private void getAlarmList(int wakeupHour, int bedHour) {
        for (int i = 0; i < 4; i++) {
            Alarm morningAlarm = new Alarm(1, "",  DataHandler.getFormattedTime(wakeupHour, i * 10), "1111111", "Default", 1, 1);
            alarmList.add(morningAlarm);
        }

        int bedHourBefore = (bedHour - 1) < 0 ?
                24 + (bedHour - 1) :
                bedHour - 1;

        Alarm bedtimeAlarmBefore = new Alarm(3,"", DataHandler.getFormattedTime(bedHourBefore, 30), "1111111", "Default", 1, 1);
        Alarm bedtimeAlarm = new Alarm(3, "",DataHandler.getFormattedTime(bedHour, 0), "1111111", "Default", 1, 1);
        alarmList.add(bedtimeAlarmBefore);
        alarmList.add(bedtimeAlarm);

        //TODO create these based on a question?
        if (configurationList.get(0).getValue() == "Yes.") {
            db.answerDao()
                    .loadValuesByQuestionIds(new int[]{4})
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            answerData -> {
//                                List<Integer> napTimes = StringHandler.getIntsFromString(answerData.get(0));
//
//                                Alarm napAlarm1 = new Alarm(2, napTimes.get(0), "1 2 3 4 5 6 7", "Default");
//                                alarmList.add(napAlarm1);
//
//                                Alarm napAlarm2 = new Alarm(2, napTimes.get(1), "1 2 3 4 5 6 7", "Default");
//                                alarmList.add(napAlarm2);

                                getNotificationList(wakeupHour, bedHour);
                            },
                            Throwable::printStackTrace
                    );
        }
        else {
            getNotificationList(wakeupHour, bedHour);
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

    private void getNotificationList(int wakeupHour, int bedHour) {
        int unwindHour = (bedHour - 2) < 0 ?
                24 + (bedHour - 2) :
                bedHour - 2;

        Notification unwindNotification = new Notification(
                "It's almost bedtime!",
                "You have 2 hours until you need to go to sleep. How about you put your phone away and take some time to unwind?",
                unwindHour + ":00",
                1
        );

        this.notificationList.add(unwindNotification);

        Notification sleepDiaryWakeupTimeNotification = new Notification(
                "It's time to fill in your sleep diary!",
                "Tap here to open your morning sleep diary.",
                wakeupHour + ":45",
                1
        );

        this.notificationList.add(sleepDiaryWakeupTimeNotification);

        int sleepDiaryBedtime = (bedHour - 1) < 0 ?
                24 + (bedHour - 1) :
                bedHour - 1;

        Notification sleepDiaryBedtimeNotification = new Notification(
                "It's time to fill in your sleep diary!",
                "Tap here to open your bedtime sleep diary.",
                sleepDiaryBedtime + ":45",
                1
        );

        this.notificationList.add(sleepDiaryBedtimeNotification);

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
                                a.schedule(App.getContext());
                            }

                            for (int i = 0; i < notificationIds.size(); i++) {
                                notificationList.get(i).setId(notificationIds.get(i).intValue());
                                notificationList.get(i).schedule(App.getContext());
                            }

                            Intent intent = new Intent(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            NavHostFragment.findNavController(this.fragment).navigate(R.id.finishQuestionnaireAction);
                        },
                        Throwable::printStackTrace
                );
    }
}
