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
            Alarm morningAlarm = new Alarm(
                    1,
                    "It's time to wake up!",
                    DataHandler.getFormattedTime(wakeupHour, i * 10),
                    "1111111",
                    "Default",
                    1,
                    1
            );
            alarmList.add(morningAlarm);
        }

        ZonedDateTime bedHourBefore = ZonedDateTime.now().withHour(bedHour).withMinute(0).minusHours(1);

        Alarm bedtimeAlarmBefore = new Alarm(
                3,
                "It's almost bedtime!",
                DataHandler.getFormattedTime(bedHourBefore.getHour(), 30),
                "1111111",
                "Default",
                1,
                1
        );
        alarmList.add(bedtimeAlarmBefore);

        Alarm bedtimeAlarm = new Alarm(
                3,
                "It's bedtime!",
                DataHandler.getFormattedTime(bedHour, 0),
                "1111111",
                "Default",
                1,
                1
        );
        alarmList.add(bedtimeAlarm);

        if (configurationList.get(0).getValue().equals("Yes.")) {
            db.answerDao()
                    .loadValuesByQuestionIds(new int[]{25})
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            answerData -> {
                                List<Integer> napTimes = DataHandler.getIntsFromString(answerData.get(0));

                                Alarm napAlarmBefore = new Alarm(
                                        2,
                                        "It's time for your nap!",
                                        DataHandler.getFormattedTime(napTimes.get(0), napTimes.get(1)),
                                        "1111111",
                                        "Default",
                                        1,
                                        1
                                );
                                alarmList.add(napAlarmBefore);

                                ZonedDateTime napWakeupTime = ZonedDateTime.now().withHour(napTimes.get(0)).withMinute(napTimes.get(1)).plusMinutes(30);

                                Alarm napAlarmAfter = new Alarm(
                                        2,
                                        "It's time to wake up from your nap!",
                                        DataHandler.getFormattedTime(napWakeupTime.getHour(), napWakeupTime.getMinute()),
                                        "1111111",
                                        "Default",
                                        1,
                                        1
                                );
                                alarmList.add(napAlarmAfter);

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
        ZonedDateTime unwindHour = ZonedDateTime.now().withHour(bedHour).withMinute(0).minusHours(2);

        Notification unwindNotification = new Notification(
                "It's almost bedtime!",
                "There are 2 hours left until your bedtime. How about you take some time to unwind? Tap here for suggestions.",
                DataHandler.getFormattedTime(unwindHour.getHour(), 0),
                1,
                R.id.relaxingActivitiesSuggestionsFragment
        );

        this.notificationList.add(unwindNotification);

        Notification sleepDiaryWakeupTimeNotification = new Notification(
                "It's time to fill in your morning sleep diary!",
                "Tap here to open it.",
                DataHandler.getFormattedTime(wakeupHour, 45),
                1,
                R.id.morningSleepDiaryFragment
        );

        this.notificationList.add(sleepDiaryWakeupTimeNotification);

        ZonedDateTime sleepDiaryWakeupTimeReminder = ZonedDateTime.now().withHour(wakeupHour).withMinute(0).plusHours(2);

        Notification sleepDiaryWakeupTimeReminderNotification = new Notification(
                "You still haven't filled in your morning sleep diary.",
                "Tap here to open it.",
                DataHandler.getFormattedTime(sleepDiaryWakeupTimeReminder.getHour(), 0),
                1,
                R.id.morningSleepDiaryFragment
        );

        this.notificationList.add(sleepDiaryWakeupTimeReminderNotification);

        ZonedDateTime sleepDiaryBedtime = ZonedDateTime.now().withHour(bedHour).withMinute(0).minusHours(1);

        Notification sleepDiaryBedtimeNotification = new Notification(
                "It's time to fill in your bedtime sleep diary!",
                "Tap here to open it.",
                DataHandler.getFormattedTime(sleepDiaryBedtime.getHour(), 45),
                1,
                R.id.bedtimeSleepDiaryFragment
        );

        this.notificationList.add(sleepDiaryBedtimeNotification);

        Notification sleepDiaryBedtimeReminderNotification = new Notification(
                "You still haven't filled in your bedtime sleep diary.",
                "Tap here to open it.",
                DataHandler.getFormattedTime(wakeupHour, 45),
                1,
                R.id.bedtimeSleepDiaryFragment
        );

        this.notificationList.add(sleepDiaryBedtimeReminderNotification);

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
