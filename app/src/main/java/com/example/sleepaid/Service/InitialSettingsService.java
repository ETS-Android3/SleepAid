package com.example.sleepaid.Service;

import android.content.Intent;
import android.provider.ContactsContract;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sleepaid.Activity.QuestionnaireScreen;
import com.example.sleepaid.App;
import com.example.sleepaid.Database.Alarm.Alarm;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Database.Configuration.Configuration;
import com.example.sleepaid.Database.Goal.Goal;
import com.example.sleepaid.Database.Notification.Notification;
import com.example.sleepaid.Handler.DataHandler;
import com.example.sleepaid.R;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
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
                                this.alarmList.addAll(AlarmAndNotificationService.createAlarms(3, answerData.get(0)));

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

                            Intent intent = new Intent(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            NavHostFragment.findNavController(this.fragment).navigate(R.id.finishQuestionnairesAction);
                        },
                        Throwable::printStackTrace
                );
    }
}
