package com.example.sleepaid.Service;

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

public class RepeatAlarmService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        AppDatabase db = AppDatabase.getDatabase(App.getContext());

        db.alarmDao()
                .loadAllByIds(new int[]{intent.getIntExtra("ID", 0)})
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

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
