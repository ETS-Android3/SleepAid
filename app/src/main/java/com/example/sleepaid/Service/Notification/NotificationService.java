package com.example.sleepaid.Service.Notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.sleepaid.Activity.MainMenuScreen;
import com.example.sleepaid.App;
import com.example.sleepaid.R;

public class NotificationService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int notificationId = intent.getIntExtra("ID", 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, App.NOTIFICATION_CHANNEL_ID)
                .setContentTitle(intent.getStringExtra("NAME"))
                .setContentText(intent.getStringExtra("CONTENT"))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.sleep_aid)
                .setAutoCancel(true);

        if (intent.hasExtra("DESTINATION")) {
            int destination = intent.getIntExtra("DESTINATION", 0);
            int parentDestination;

            switch (destination) {
                case R.id.morningSleepDiaryFragment:
                case R.id.bedtimeSleepDiaryFragment:
                    parentDestination = R.id.sleepDiaryFragment;
                    break;

                default:
                    parentDestination = R.id.sleepDataFragment;
                    break;
            }

            Intent resultIntent = new Intent(this, MainMenuScreen.class);
            resultIntent.putExtra("PARENT_DESTINATION", parentDestination);
            resultIntent.putExtra("DESTINATION", destination);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, resultIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

            notificationBuilder.setContentIntent(pendingIntent).setOngoing(true);
        } else {
            notificationBuilder.setOngoing(false);
        }

        Notification notification = notificationBuilder.build();
        startForeground(notificationId, notification);
        stopForeground(STOP_FOREGROUND_DETACH);

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
