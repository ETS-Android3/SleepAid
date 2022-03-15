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

        String hour = calendar.get(Calendar.HOUR_OF_DAY) < 10 ?
                "0" + calendar.get(Calendar.HOUR_OF_DAY) :
                Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));

        String minute = calendar.get(Calendar.MINUTE) < 10 ?
                "0" + calendar.get(Calendar.MINUTE) :
                Integer.toString(calendar.get(Calendar.MINUTE));

        Alarm alarm = new Alarm(
                intent.getIntExtra("TYPE", 1),
                "Snooze",
                hour + ":" + minute,
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
