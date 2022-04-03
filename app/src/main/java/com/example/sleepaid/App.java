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

    @Override
    public void onCreate() {
        super.onCreate();

        this.context = getApplicationContext();

        createAlarmChannel();
        createNotificationChannel();
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
        this.alarmSounds.put("Beautiful", R.raw.beautiful_ringtone);
        this.alarmSounds.put("Bright", R.raw.bright_ringtone);
        this.alarmSounds.put("Buzzer", R.raw.buzzer_ringtone);
        this.alarmSounds.put("Homecoming", R.raw.homecoming_ringtone);
        this.alarmSounds.put("Smart", R.raw.smart_ringtone);
    }
}
