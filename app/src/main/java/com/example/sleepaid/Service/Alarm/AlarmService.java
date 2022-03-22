package com.example.sleepaid.Service.Alarm;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.sleepaid.App;
import com.example.sleepaid.Handler.DataHandler;
import com.example.sleepaid.R;

public class AlarmService extends Service {
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    @Override
    public void onCreate() {
        super.onCreate();

        this.mediaPlayer = new MediaPlayer();
        this.vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, AlarmActionBroadcastReceiverService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, intent.getIntExtra("ID", 0), notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Intent snoozeIntent = new Intent(this, AlarmActionBroadcastReceiverService.class);
        snoozeIntent.setAction("SNOOZE");
        snoozeIntent.putExtra("SOUND", intent.getStringExtra("SOUND"));
        snoozeIntent.putExtra("VIBRATE", intent.getIntExtra("VIBRATE", 1));

        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(this, intent.getIntExtra("ID", 0), snoozeIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Intent dismissIntent = new Intent(this, AlarmActionBroadcastReceiverService.class);
        dismissIntent.setAction("DISMISS");

        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(this, intent.getIntExtra("ID", 0), dismissIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, App.ALARM_CHANNEL_ID)
                .setContentTitle(intent.getStringExtra("NAME"))
                .setContentText(intent.getStringExtra("TIME"))
                .setSmallIcon(R.drawable.alarm_icon)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.delete_icon, "Snooze", snoozePendingIntent)
                .addAction(R.drawable.delete_icon, "Dismiss", dismissPendingIntent)
                .build();

        DataHandler.playAlarmSound(this.mediaPlayer, this, App.getSound(intent.getStringExtra("SOUND")), true);

        if (intent.getIntExtra("VIBRATE", 1) == 1) {
            long[] pattern = {0, 750, 1000};
            this.vibrator.vibrate(
                    VibrationEffect.createWaveform(pattern, 0),
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .build());
        }

        startForeground(1, notification);

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (this.mediaPlayer.isPlaying()) {
            this.mediaPlayer.stop();
        }

        this.vibrator.cancel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
