package com.example.sleepaid.Service.Alarm;

import com.example.sleepaid.App;
import com.example.sleepaid.Database.AppDatabase;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RecurringAlarmService {
    public void scheduleRepeat(int id) {
        AppDatabase db = AppDatabase.getDatabase(App.getContext());

        db.alarmDao()
                .loadAllByIds(new int[]{id})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        alarm -> {
                            if (!alarm.isEmpty()) {
                                alarm.get(0).scheduleRecurring(App.getContext());
                            }
                        },
                        Throwable::printStackTrace
                );
    }
}
