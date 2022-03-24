package com.example.sleepaid;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import java.util.HashMap;

public class App extends Application {
    private static Context context;

    public static final String ALARM_CHANNEL_ID = "ALARM_CHANNEL";
    public static final String NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL";

    private static HashMap<String, Integer> alarmSounds;

    private static int notificationSound;
    private static boolean notificationVibrate;

    @Override
    public void onCreate() {
        super.onCreate();

        this.context = getApplicationContext();

        createAlarmChannel();
        createNotificationChannel();
        createAlarmSounds();

//        this.notificationSound = R.raw.default_notification_sound;
//        this.notificationVibrate = true;
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

    public static int getNotificationSound() {
        return notificationSound;
    }

    public static boolean getNotificationVibrate() {
        return notificationVibrate;
    }

    public static void setNotificationSound(int notificationSound) {
        App.notificationSound = notificationSound;
    }

    public static void setNotificationVibrate(boolean notificationVibrate) {
        App.notificationVibrate = notificationVibrate;
    }

    private void createAlarmChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                ALARM_CHANNEL_ID,
                "Alarm Channel",
                NotificationManager.IMPORTANCE_HIGH
        );
        serviceChannel.setSound(null,null);

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Notification Channel",
                NotificationManager.IMPORTANCE_HIGH
        );

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }

    private void createAlarmSounds() {
        this.alarmSounds = new HashMap<>();

        this.alarmSounds.put("Default", R.raw.default_ringtone);
        this.alarmSounds.put("Glory", R.raw.glory_ringtone);
    }
}
