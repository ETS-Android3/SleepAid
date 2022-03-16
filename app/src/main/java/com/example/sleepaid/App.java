package com.example.sleepaid;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressLint("NewApi")
public class App extends Application {
    private static Context context;

    public static final String CHANNEL_ID = "ALARM_SERVICE_CHANNEL";

    private static HashMap<String, Integer> alarmSounds;

    @Override
    public void onCreate() {
        super.onCreate();

        this.context = getApplicationContext();
        createNotificationChannnel();
        createAlarmSounds();
    }

    public static Context getContext() {
        return context;
    }

    public static int getSound(String soundName) {
        return alarmSounds.get(soundName);
    }

    public static HashMap<String, Integer> getSounds() {
        return alarmSounds;
    }

    private void createNotificationChannnel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Alarm Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }

    private void createAlarmSounds() {
        this.alarmSounds = new HashMap<>();

        this.alarmSounds.put("Glory", R.raw.glory_ringtone);
        this.alarmSounds.put("Default", R.raw.default_ringtone);
    }
}
