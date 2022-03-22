package com.example.sleepaid.Service.Alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.sleepaid.Database.Alarm.Alarm;
import com.example.sleepaid.Handler.DataHandler;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;


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
        ZonedDateTime date = ZonedDateTime.now()
                .plusMinutes(5)
                .truncatedTo(ChronoUnit.MINUTES);

        Alarm alarm = new Alarm(
                intent.getIntExtra("TYPE", 1),
                "Snooze",
                DataHandler.getFormattedTime(date.getHour(), date.getMinute()),
                "0000000",
                intent.getStringExtra("SOUND"),
                intent.getIntExtra("VIBRATE", 1),
                1
        );
        alarm.setId((int) date.toInstant().toEpochMilli());

        alarm.schedule(context);

        Intent intentService = new Intent(context, AlarmService.class);
        context.stopService(intentService);
    }

    private void dismissAlarm(Context context) {
        Intent intentService = new Intent(context, AlarmService.class);
        context.stopService(intentService);
    }
}
