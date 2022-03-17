package com.example.sleepaid.Service.Notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Vibrator;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.sleepaid.App;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Database.Configuration.Configuration;
import com.example.sleepaid.R;
import com.example.sleepaid.Service.Alarm.AlarmActionBroadcastReceiverService;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NotificationService extends Service {
//    private MediaPlayer mediaPlayer;
//    private Vibrator vibrator;

    @Override
    public void onCreate() {
        super.onCreate();

//        this.vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = new NotificationCompat.Builder(this, App.NOTIFICATION_CHANNEL_ID)
                .setContentTitle(intent.getStringExtra("NAME"))
                .setContentText(intent.getStringExtra("CONTENT"))
                //TODO change this icon
                .setSmallIcon(R.drawable.alarm_icon)
                .build();

        //TODO check if we do need these or if it takes it from the phone settings
//        this.mediaPlayer = MediaPlayer.create(this, App.getNotificationSound());
//        this.mediaPlayer.setLooping(false);
//        this.mediaPlayer.start();
//
//        if (App.getNotificationVibrate()) {
//            this.vibrator.vibrate(100);
//        }

        startForeground(1, notification);
        stopForeground(false);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

//        mediaPlayer.stop();
//        vibrator.cancel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
