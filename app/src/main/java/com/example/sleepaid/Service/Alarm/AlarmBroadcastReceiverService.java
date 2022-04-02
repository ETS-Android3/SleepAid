package com.example.sleepaid.Service.Alarm;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class AlarmBroadcastReceiverService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null &&
                (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ||
                intent.getAction().equals(AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED))) {
            startRescheduleAlarmsService(context);
        }
        else {
            startAlarmService(context, intent);

            if (intent.getBooleanExtra("RECURRING", false)) {
                startRepeatAlarmService(intent);
            }
        }
    }

    private void startAlarmService(Context context, Intent intent) {
        Intent intentService = new Intent(context, AlarmService.class);

        intentService.putExtra("ID", intent.getIntExtra("ID", 0));
        intentService.putExtra("NAME", intent.getStringExtra("NAME"));
        intentService.putExtra("TIME", intent.getStringExtra("TIME"));
        intentService.putExtra("SOUND", intent.getStringExtra("SOUND"));
        intentService.putExtra("VIBRATE", intent.getIntExtra("VIBRATE", 1));

        context.startForegroundService(intentService);
    }

    private void startRepeatAlarmService(Intent intent) {
        RecurringAlarmService recurringAlarmService = new RecurringAlarmService();
        recurringAlarmService.scheduleRepeat(intent.getIntExtra("ID", 0));
    }

    private void startRescheduleAlarmsService(Context context) {
        Intent intentService = new Intent(context, RescheduleAlarmsService.class);
        context.startService(intentService);
    }
}
