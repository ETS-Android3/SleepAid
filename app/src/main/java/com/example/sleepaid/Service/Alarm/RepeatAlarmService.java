package com.example.sleepaid.Service.Alarm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.Observer;

import com.example.sleepaid.App;
import com.example.sleepaid.Database.Alarm.Alarm;
import com.example.sleepaid.Database.AppDatabase;

import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RepeatAlarmService {
    public void scheduleRepeat(int id) {
        AppDatabase db = AppDatabase.getDatabase(App.getContext());

        db.alarmDao()
                .loadAllByIds(new int[]{id})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        alarm -> {
                            if (!alarm.isEmpty()) {
                                alarm.get(0).scheduleRepeat(App.getContext());
                            }
                        },
                        Throwable::printStackTrace
                );
    }
}
