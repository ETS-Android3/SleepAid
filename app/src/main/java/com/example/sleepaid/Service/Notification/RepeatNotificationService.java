package com.example.sleepaid.Service.Notification;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.sleepaid.App;
import com.example.sleepaid.Database.AppDatabase;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RepeatNotificationService {
    public void scheduleRepeat(int id) {
        AppDatabase db = AppDatabase.getDatabase(App.getContext());

        db.notificationDao()
                .loadAllByIds(new int[]{id})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        notification -> {
                            if (!notification.isEmpty()) {
                                notification.get(0).scheduleRepeat(App.getContext());
                            }
                        },
                        Throwable::printStackTrace
                );
    }
}
