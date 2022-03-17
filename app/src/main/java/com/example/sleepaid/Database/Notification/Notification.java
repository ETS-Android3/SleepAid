package com.example.sleepaid.Database.Notification;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.sleepaid.App;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Handler.DataHandler;
import com.example.sleepaid.Service.Notification.NotificationBroadcastReceiverService;

import java.util.Calendar;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@SuppressLint("NewApi")
@Entity
public class Notification {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "notificationId")
    public int id;

    @NonNull
    public String name;
    @NonNull
    public String content;
    @NonNull
    public String time;
    @NonNull
    public int isDaily;

    public Notification(String name,
                        String content,
                        String time,
                        int isDaily) {
        this.name = name;
        this.content = content;
        this.time = time;
        this.isDaily = isDaily;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getContent() {
        return this.content;
    }

    public String getTime() {
        return this.time;
    }

    public int getIsDaily() {
        return this.isDaily;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setIsDaily(int isDaily) {
        this.isDaily = isDaily;
    }

    public void schedule(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, NotificationBroadcastReceiverService.class);

        intent.putExtra("ID", this.id);
        intent.putExtra("NAME", this.name);
        intent.putExtra("CONTENT", this.content);
        intent.putExtra("DAILY", this.isDaily == 1);

        PendingIntent notificationPendingIntent = PendingIntent.getBroadcast(context, this.id, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();

        List<Integer> time = DataHandler.getIntsFromString(this.time);
        calendar.set(Calendar.HOUR_OF_DAY, time.get(0));
        calendar.set(Calendar.MINUTE, time.get(1));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // If notification time has already passed, increment day by 1 and schedule it
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                notificationPendingIntent
        );
    }

    public void scheduleRepeat(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, NotificationBroadcastReceiverService.class);

        intent.putExtra("ID", this.id);
        intent.putExtra("NAME", this.name);
        intent.putExtra("CONTENT", this.content);
        intent.putExtra("TIME", this.time);
        intent.putExtra("DAILY", this.isDaily == 1);

        PendingIntent notificationPendingIntent = PendingIntent.getBroadcast(context, this.id, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        List<Integer> time = DataHandler.getIntsFromString(this.time);
        calendar.set(Calendar.HOUR_OF_DAY, time.get(0));
        calendar.set(Calendar.MINUTE, time.get(1));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                notificationPendingIntent
        );
    }

    public void cancel(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationBroadcastReceiverService.class);
        PendingIntent notificationPendingIntent = PendingIntent.getBroadcast(context, this.id, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(notificationPendingIntent);
    }
}
