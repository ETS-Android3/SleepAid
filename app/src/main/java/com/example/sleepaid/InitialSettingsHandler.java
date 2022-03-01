package com.example.sleepaid;

import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sleepaid.Database.Alarm.Alarm;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Database.Configuration.Configuration;
import com.example.sleepaid.Database.Goal.Goal;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class InitialSettingsHandler {
    Fragment fragment;
    AppDatabase db;

    List<Configuration> configurationList;
    List<Goal> goalList;
    List<Alarm> alarmList;

    public InitialSettingsHandler(Fragment fragment, AppDatabase db) {
        this.fragment = fragment;
        this.db = db;

        this.configurationList = new ArrayList<>();
        this.goalList = new ArrayList<>();
        this.alarmList = new ArrayList<>();
    }

    public void getSettings() {
        getConfigurationList();
    }

    private void createSettings() {
        createConfigurations();
    }

    private void getConfigurationList() {
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

                            int minBedtime = DataHandler.getIntsFromString(bedtimes.get(0)).get(0);
                            int minWakeupTime = DataHandler.getIntsFromString(wakeupTimes.get(0)).get(0);

                            int duration = minWakeupTime - minBedtime <= 0 ?
                                    24 + (minWakeupTime - minBedtime) :
                                    minWakeupTime - minBedtime;
                            Goal sleepDuration = new Goal("Sleep duration", duration + "h", duration + "h");
                            goalList.add(sleepDuration);

                            getAlarmList(minWakeupTime, minBedtime);
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

    private void getAlarmList(int wakeupTime, int bedTime) {
        for (int i = 0; i < 4; i++) {
            String newWakeupTime = (i == 0) ? wakeupTime + ":00" : wakeupTime + ":" + i * 10;

            Alarm morningAlarm = new Alarm(1,  newWakeupTime + " am", "M T W T F S S", "default");
            alarmList.add(morningAlarm);
        }

        //TODO figure out AM and PM
        Alarm bedtimeAlarmBefore = new Alarm(3, (bedTime - 1) < 12 ? (bedTime - 1) + ":30 pm" : (bedTime - 1) + ":30 am", "M T W T F S S", "default");
        Alarm bedtimeAlarm = new Alarm(3, bedTime < 12 ? bedTime + ":00 pm" : bedTime + ":00 am", "M T W T F S S", "default");
        alarmList.add(bedtimeAlarmBefore);
        alarmList.add(bedtimeAlarm);

        if (configurationList.get(0).getValue() == "Yes.") {
            db.answerDao()
                    .loadValuesByQuestionIds(new int[]{4})
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            answerData -> {
                                //TODO: figure out how to store this better
//                                List<Integer> napTimes = StringHandler.getIntsFromString(answerData.get(0));
//
//                                Alarm napAlarm1 = new Alarm(2, napTimes.get(0), "1 2 3 4 5 6 7", "default");
//                                alarmList.add(napAlarm1);
//
//                                Alarm napAlarm2 = new Alarm(2, napTimes.get(1), "1 2 3 4 5 6 7", "default");
//                                alarmList.add(napAlarm2);

                                createSettings();
                            },
                            Throwable::printStackTrace
                    );
        }
        else {
            createSettings();
        }
    }

    private void createAlarms() {
        db.alarmDao()
                .insert(alarmList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            Intent intent = new Intent(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            NavHostFragment.findNavController(this.fragment).navigate(R.id.finishQuestionnaireAction);
                        },
                        Throwable::printStackTrace
                );
    }
}
