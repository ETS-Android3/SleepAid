package com.example.sleepaid.Service.BlueLightFilter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import com.example.sleepaid.App;

import java.time.ZonedDateTime;

public class BlueLightFilterBroadcastReceiverService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, BlueLightFilterService.class);

        if (intent.getAction() != null && intent.getAction().equals("STOP")) {
            if (BlueLightFilterService.isRunning()) {
                BlueLightFilterService.setIsRunning(false);
                context.stopService(serviceIntent);
            }

            return;
        }

        if (intent.getAction() != null &&
                (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ||
                        intent.getAction().equals(AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED) ||
                        intent.getAction().equals("android.intent.action.MY_PACKAGE_REPLACED"))) {
            if (Settings.canDrawOverlays(App.getContext())) {
                if (ZonedDateTime.now().getHour() >= 20 ||
                        (ZonedDateTime.now().getHour() <= 7  && ZonedDateTime.now().getMinute() < 30)) {
                    if (!BlueLightFilterService.isRunning()) {
                        BlueLightFilterService.setIsRunning(true);
                        context.startForegroundService(serviceIntent);

                        this.scheduleFilter(context, 1);
                        this.scheduleStop(context, 1);
                    }
                }

                if (ZonedDateTime.now().getHour() < 20) {
                    this.scheduleFilter(context, 0);
                }
            }

            if (ZonedDateTime.now().getHour() <= 7 && BlueLightFilterService.isRunning()) {
                this.scheduleStop(context, 0);
            }

            return;
        }

        if (Settings.canDrawOverlays(App.getContext()) && !BlueLightFilterService.isRunning()) {
            BlueLightFilterService.setIsRunning(true);
            context.startForegroundService(serviceIntent);

            this.scheduleFilter(context, 1);
            this.scheduleStop(context, 1);
        }
    }

    private void scheduleFilter(Context context, int days) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()) {
            ZonedDateTime startTime = ZonedDateTime.now()
                    .withHour(20)
                    .withMinute(0);

            if (days != 0) {
                startTime = startTime.plusDays(days);
            }

            Intent startIntent = new Intent(context, BlueLightFilterBroadcastReceiverService.class);

            PendingIntent startPendingIntent = PendingIntent.getBroadcast(context, (int) startTime.toInstant().toEpochMilli(), startIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.setAlarmClock(
                    new AlarmManager.AlarmClockInfo(startTime.toInstant().toEpochMilli(), startPendingIntent),
                    startPendingIntent
            );
        }
    }

    private void scheduleStop(Context context, int days) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()) {
            Intent stopIntent = new Intent(context, BlueLightFilterBroadcastReceiverService.class);

            ZonedDateTime stopTime = ZonedDateTime.now()
                    .withHour(7)
                    .withMinute(30);

            if (days != 0) {
                stopTime = stopTime.plusDays(days);
            }

            stopIntent.setAction("STOP");

            PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, (int) stopTime.toInstant().toEpochMilli(), stopIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.setAlarmClock(
                    new AlarmManager.AlarmClockInfo(stopTime.toInstant().toEpochMilli(), stopPendingIntent),
                    stopPendingIntent
            );
        }
    }
}