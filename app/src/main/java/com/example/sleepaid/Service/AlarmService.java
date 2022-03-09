package com.example.sleepaid.Service;

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
import com.example.sleepaid.R;

import java.util.HashMap;

public class AlarmService extends Service {
    private HashMap<String, Integer> sounds;

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    @Override
    public void onCreate() {
        super.onCreate();

        this.sounds = new HashMap<>();
        sounds.put("default", R.raw.alarm);

        this.vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, AlarmActionBroadcastReceiverService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE);

        Intent snoozeIntent = new Intent(this, AlarmActionBroadcastReceiverService.class);
        snoozeIntent.setAction("SNOOZE");
        snoozeIntent.putExtra("SOUND", intent.getStringExtra("SOUND"));
        snoozeIntent.putExtra("VIBRATE", intent.getIntExtra("VIBRATE", 1));

        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(this, 0, snoozeIntent, PendingIntent.FLAG_MUTABLE);

        Intent dismissIntent = new Intent(this, AlarmActionBroadcastReceiverService.class);
        dismissIntent.setAction("DISMISS");
        dismissIntent.putExtra("SOUND", intent.getStringExtra("SOUND"));
        dismissIntent.putExtra("VIBRATE", intent.getIntExtra("VIBRATE", 1));

        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(this, 0, dismissIntent, PendingIntent.FLAG_MUTABLE);

        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setContentTitle(intent.getStringExtra("NAME"))
                .setContentText(intent.getStringExtra("TIME"))
                .setSmallIcon(R.drawable.alarm_icon)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.delete_icon, "Snooze", snoozePendingIntent)
                .addAction(R.drawable.delete_icon, "Dismiss", dismissPendingIntent)
                .build();

        System.out.println(intent.getStringExtra("SOUND"));
        this.mediaPlayer = MediaPlayer.create(this, this.sounds.get(intent.getStringExtra("SOUND")));
        this.mediaPlayer.setLooping(true);
        this.mediaPlayer.start();

        if (intent.getIntExtra("VIBRATE", 1) == 1) {
            long[] pattern = {0, 100, 1000};
            this.vibrator.vibrate(pattern, 0);
        }

        startForeground(1, notification);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mediaPlayer.stop();
        vibrator.cancel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
