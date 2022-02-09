package com.example.sleepaid;

import android.app.Activity;
import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

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
                            System.out.println(answerData.get(0));
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
                            List<Integer> bedTimes = StringHandler.getIntsFromString(answerData.get(0));
                            Goal bedTimeMin = new Goal("bedTimeMin", Integer.toString(bedTimes.get(0)));
                            goalList.add(bedTimeMin);

                            Goal bedTimeMax = new Goal("bedTimeMax", Integer.toString(bedTimes.get(1)));
                            goalList.add(bedTimeMax);

                            List<Integer> wakeUpTimes = StringHandler.getIntsFromString(answerData.get(1));
                            Goal wakeUpTimeMin = new Goal("wakeUpTimeMin", Integer.toString(wakeUpTimes.get(0)));
                            goalList.add(wakeUpTimeMin);

                            Goal wakeUpTimeMax = new Goal("wakeUpTimeMax", Integer.toString(wakeUpTimes.get(1)));
                            goalList.add(wakeUpTimeMax);

                            int duration = bedTimes.get(0) <= 12 ?
                                    12 + (wakeUpTimes.get(0) - bedTimes.get(0)) :
                                    (wakeUpTimes.get(0) - bedTimes.get(0));
                            Goal sleepDuration = new Goal("sleepDuration", Integer.toString(duration));
                            goalList.add(sleepDuration);

                            getAlarmList(wakeUpTimes.get(0), bedTimes.get(0));
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

    private void getAlarmList(int wakeUpTime, int bedTime) {
        Alarm morningAlarm = new Alarm(1, wakeUpTime + " am", "1 2 3 4 5 6 7", "default");
        alarmList.add(morningAlarm);

        Alarm bedtimeAlarm = new Alarm(3, bedTime < 12 ? bedTime + " pm" : bedTime + " am", "1 2 3 4 5 6 7", "default");
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
