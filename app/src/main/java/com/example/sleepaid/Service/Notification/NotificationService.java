package com.example.sleepaid.Service.Notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.navigation.NavDeepLinkBuilder;

import com.example.sleepaid.Activity.MainMenuScreen;
import com.example.sleepaid.App;
import com.example.sleepaid.R;

public class NotificationService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification;

        if (intent.hasExtra("DESTINATION")) {
            Bundle args = new Bundle();
            args.putInt("DESTINATION", intent.getIntExtra("DESTINATION", R.id.morningSleepDiaryFragment));

            PendingIntent pendingIntent = new NavDeepLinkBuilder(App.getContext())
                    .setComponentName(MainMenuScreen.class)
                    .setGraph(R.navigation.main_menu_screen_graph)
                    .setDestination(R.id.sleepDiaryFragment)
                    .setArguments(args)
                    .createPendingIntent();

            notification = new NotificationCompat.Builder(this, App.NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(intent.getStringExtra("NAME"))
                    .setContentText(intent.getStringExtra("CONTENT"))
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setAutoCancel(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    //TODO change this icon
                    .setSmallIcon(R.drawable.sleep_aid)
                    .build();
        } else {
            notification = new NotificationCompat.Builder(this, App.NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(intent.getStringExtra("NAME"))
                    .setContentText(intent.getStringExtra("CONTENT"))
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    //TODO change this icon
                    .setSmallIcon(R.drawable.sleep_aid)
                    .build();
        }

        startForeground(1, notification);
        stopForeground(false);

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
