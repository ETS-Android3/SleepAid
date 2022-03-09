package com.example.sleepaid.Service;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.sleepaid.Database.Alarm.Alarm;

import java.util.Calendar;
import java.util.Random;

@SuppressLint("NewApi")
public class AlarmActionBroadcastReceiverService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("SNOOZE")) {
            this.snoozeAlarm(context, intent);
        }
        else {
            this.dismissAlarm(context);
        }
    }

    private void snoozeAlarm(Context context, Intent intent) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, 5);

        Alarm alarm = new Alarm(
                new Random().nextInt(Integer.MAX_VALUE),
                "Snooze",
                calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE),
                "0000000",
                intent.getStringExtra("SOUND"),
                intent.getIntExtra("VIBRATE", 1),
                1
        );

        alarm.schedule(context);

        Intent intentService = new Intent(context, AlarmService.class);
        context.stopService(intentService);
    }

    private void dismissAlarm(Context context) {
        Intent intentService = new Intent(context, AlarmService.class);
        context.stopService(intentService);
    }
}
