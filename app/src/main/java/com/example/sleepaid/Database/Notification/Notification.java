package com.example.sleepaid.Database.Notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.sleepaid.Handler.DataHandler;
import com.example.sleepaid.Service.Notification.NotificationBroadcastReceiverService;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;


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
    @NonNull
    public int destination;

    public Notification(String name,
                        String content,
                        String time,
                        int isDaily,
                        int destination) {
        this.name = name;
        this.content = content;
        this.time = time;
        this.isDaily = isDaily;
        this.destination = destination;
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

    public void setId(int id) {
        this.id = id;
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

    public void schedule(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, NotificationBroadcastReceiverService.class);

        intent.putExtra("ID", this.id);
        intent.putExtra("NAME", this.name);
        intent.putExtra("CONTENT", this.content);
        intent.putExtra("DAILY", this.isDaily == 1);
        if (this.destination != 0) {
            intent.putExtra("DESTINATION", this.destination);
        }

        PendingIntent notificationPendingIntent = PendingIntent.getBroadcast(context, this.id, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        List<Integer> time = DataHandler.getIntsFromString(this.time);
        ZonedDateTime date = ZonedDateTime.now()
                .withHour(time.get(0))
                .withMinute(time.get(1))
                .truncatedTo(ChronoUnit.MINUTES);

        // If alarm time has already passed increment day by 1 and schedule it
        if (date.toInstant().toEpochMilli() <= System.currentTimeMillis()) {
            date = date.plusDays(1);
        }

        alarmManager.setAlarmClock(
                new AlarmManager.AlarmClockInfo(date.toInstant().toEpochMilli(), notificationPendingIntent),
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
        if (this.destination != 0) {
            intent.putExtra("DESTINATION", this.destination);
        }

        PendingIntent notificationPendingIntent = PendingIntent.getBroadcast(context, this.id, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        List<Integer> time = DataHandler.getIntsFromString(this.time);
        ZonedDateTime date = ZonedDateTime.now()
                .withHour(time.get(0))
                .withMinute(time.get(1))
                .plusDays(1)
                .truncatedTo(ChronoUnit.MINUTES);

        alarmManager.setAlarmClock(
                new AlarmManager.AlarmClockInfo(date.toInstant().toEpochMilli(), notificationPendingIntent),
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
