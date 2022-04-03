package com.example.sleepaid.Service.Notification;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.sleepaid.App;
import com.example.sleepaid.Database.AppDatabase;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class NotificationBroadcastReceiverService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null &&
                (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ||
                 intent.getAction().equals(AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED))) {
            startRescheduleNotificationsService(context);
        }
        else {
            startNotificationService(context, intent);

            if (intent.getBooleanExtra("RECURRING", false)) {
                startRepeatNotificationService(intent);
            }

            if (intent.getStringExtra("NAME").contains("It's time to fill in")) {
                scheduleReminder(intent);
            }
        }
    }

    private void startNotificationService(Context context, Intent intent) {
        Intent intentService = new Intent(context, NotificationService.class);

        intentService.putExtra("ID", intent.getIntExtra("ID", 0));
        intentService.putExtra("NAME", intent.getStringExtra("NAME"));
        intentService.putExtra("CONTENT", intent.getStringExtra("CONTENT"));
        intentService.putExtra("DESTINATION", intent.getIntExtra("DESTINATION", 0));

        context.startForegroundService(intentService);
    }

    private void startRepeatNotificationService(Intent intent) {
        RepeatNotificationService repeatNotificationService = new RepeatNotificationService();
        repeatNotificationService.scheduleRepeat(intent.getIntExtra("ID", 0));
    }

    private void startRescheduleNotificationsService(Context context) {
        Intent intentService = new Intent(context, RescheduleNotificationsService.class);
        context.startService(intentService);
    }

    private void scheduleReminder(Intent intent) {
        String sleepDiary = intent.getStringExtra("NAME").contains("morning") ?
                "morning" :
                "bedtime";

        String time = intent.getStringExtra("NAME").contains("morning") ?
                "00:00" :
                "12:00";

        String name = "You still haven't filled in your " +
                sleepDiary +
                " sleep diary. You can do it until " +
                time + ".";

        AppDatabase.getDatabase(App.getContext()).notificationDao()
                .loadAllByNames(new String[]{name})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        notification -> {
                            if (!notification.isEmpty()) {
                                notification.get(0).schedule(App.getContext());
                            }
                        },
                        Throwable::printStackTrace
                );
    }
}
