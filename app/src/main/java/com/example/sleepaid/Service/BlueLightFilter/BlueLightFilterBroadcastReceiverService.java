package com.example.sleepaid.Service.BlueLightFilter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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

        if (intent.getAction() != null && intent.getAction().equals("android.intent.action.MY_PACKAGE_REPLACED")) {
            this.rescheduleFilter(context, intent);
            return;
        }

        this.scheduleFilter(context, intent, 1);
        this.scheduleStop(context, 1);

        if (Settings.canDrawOverlays(App.getContext())) {
            if (ZonedDateTime.now().getHour() >= 20 && ZonedDateTime.now().getHour() < 7) {
                context.startForegroundService(serviceIntent);
            } else if (BlueLightFilterService.isRunning()) {
                BlueLightFilterService.setIsRunning(false);
                context.stopService(serviceIntent);
            }
        }
    }

    private void rescheduleFilter(Context context, Intent intent) {
        ZonedDateTime date = ZonedDateTime.now();

        if (date.getHour() >= 20 || date.getHour() < 7) {
            if (Settings.canDrawOverlays(App.getContext())) {
                Intent serviceIntent = new Intent(context, BlueLightFilterService.class);
                context.startForegroundService(serviceIntent);
            }

            if (BlueLightFilterService.isRunning()) {
                int days = date.getHour() < 7 ?
                        0 :
                        1;

                this.scheduleStop(context, days);
            }
        } else {
            this.scheduleFilter(context, intent, 0);
        }
    }

    private void scheduleFilter(Context context, Intent intent, int days) {
        if (Settings.canDrawOverlays(App.getContext())) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            int startHour = 20;
            int startMinute = 0;

            long startTime = ZonedDateTime.now()
                    .withHour(startHour)
                    .withMinute(startMinute)
                    .plusDays(days)
                    .toInstant()
                    .toEpochMilli();

            PendingIntent startPendingIntent = PendingIntent.getBroadcast(context, (int) startTime, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.setAlarmClock(
                    new AlarmManager.AlarmClockInfo(startTime, startPendingIntent),
                    startPendingIntent
            );
        }
    }

    private void scheduleStop(Context context, int days) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent stopIntent = new Intent(context, BlueLightFilterBroadcastReceiverService.class);

        long stopTime = ZonedDateTime.now()
                .withHour(7)
                .withMinute(30)
                .plusDays(days)
                .toInstant()
                .toEpochMilli();

        stopIntent.setAction("STOP");

        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, (int) stopTime, stopIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setAlarmClock(
                new AlarmManager.AlarmClockInfo(stopTime, stopPendingIntent),
                stopPendingIntent
        );
    }
}