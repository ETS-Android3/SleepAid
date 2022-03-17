package com.example.sleepaid.Service.Alarm;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.sleepaid.Database.Alarm.Alarm;
import com.example.sleepaid.Handler.DataHandler;

import java.util.Calendar;

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
                intent.getIntExtra("TYPE", 1),
                "Snooze",
                DataHandler.getFormattedTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)),
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
