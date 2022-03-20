package com.example.sleepaid.Database.Alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.sleepaid.Database.AlarmType.AlarmType;
import com.example.sleepaid.Handler.DataHandler;
import com.example.sleepaid.Service.Alarm.AlarmBroadcastReceiverService;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.List;


@Entity(foreignKeys = {
        @ForeignKey(
                entity = AlarmType.class,
                parentColumns = "typeId",
                childColumns = "type",
                onDelete = ForeignKey.CASCADE
        )
})
public class Alarm implements Comparable<Alarm> {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "alarmId")
    public int id;

    public int type;
    @NonNull
    public String name;
    @NonNull
    public String time;
    @NonNull
    public String days;
    @NonNull
    public String sound;
    @NonNull
    public int vibrate;
    @NonNull
    public int isOn;

    public Alarm(int type,
                 String name,
                 String time,
                 String days,
                 String sound,
                 int vibrate,
                 int isOn) {
        this.type = type;
        this.name = name;
        this.time = time;
        this.days = days;
        this.sound = sound;
        this.vibrate = vibrate;
        this.isOn = isOn;
    }

    public int getId() {
        return this.id;
    }

    public int getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public String getTime() {
        return this.time;
    }

    public String getDays() {
        return this.days;
    }

    public String getSound() {
        return this.sound;
    }

    public int getVibrate() {
        return this.vibrate;
    }

    public int getIsOn() {
        return this.isOn;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public void setVibrate(int vibrate) {
        this.vibrate = vibrate;
    }

    public void setIsOn(int isOn) {
        this.isOn = isOn;
    }

    @Override
    public int compareTo(Alarm newAlarm) {
        double newAlarmTime = DataHandler.getDoubleFromTime(newAlarm.getTime());
        double currentAlarmTime = DataHandler.getDoubleFromTime(this.time);

        int result;

        if ((currentAlarmTime - newAlarmTime) < 0) {
            result = -1;
        } else if ((currentAlarmTime - newAlarmTime) > 0) {
            result = 1;
        } else {
            result = 0;
        }

        return result;
    }

    public boolean equals(Alarm newAlarm) {
        return this.name.equals(newAlarm.getName()) &&
                this.time.equals(newAlarm.getTime()) &&
                this.days.equals(newAlarm.getDays()) &&
                this.sound.equals(newAlarm.getSound()) &&
                this.vibrate == newAlarm.getVibrate();
    }

    public void schedule(Context context) {
        boolean recurring = this.days.contains("1");

        if (recurring && this.days.charAt(ZonedDateTime.now().getDayOfWeek().getValue() - 1) != '1') {
            this.scheduleRecurring(context);
            return;
        }

        List<Integer> time = DataHandler.getIntsFromString(this.time);
        System.out.println(this.time);
        ZonedDateTime date = ZonedDateTime.now()
                .withHour(time.get(0))
                .withMinute(time.get(1))
                .truncatedTo(ChronoUnit.MINUTES);

        // If alarm time has already passed and it's not a recurring alarm, increment day by 1 and schedule it.
        // If it is recurring, schedule it for the next recurring day
        if (date.toInstant().toEpochMilli() <= System.currentTimeMillis()) {
            if (!recurring) {
                date = date.plusDays(1);
            } else {
                this.scheduleRecurring(context);
                return;
            }
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmBroadcastReceiverService.class);

        intent.putExtra("ID", this.id);
        intent.putExtra("TYPE", this.type);
        intent.putExtra("NAME", this.name);
        intent.putExtra("TIME", this.time);
        intent.putExtra("SOUND", this.sound);
        intent.putExtra("VIBRATE", this.vibrate);
        intent.putExtra("RECURRING", recurring);

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, this.id, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                date.toInstant().toEpochMilli(),
                alarmPendingIntent
        );

        this.isOn = 1;
    }

    public void scheduleRecurring(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmBroadcastReceiverService.class);

        intent.putExtra("ID", this.id);
        intent.putExtra("TYPE", this.type);
        intent.putExtra("NAME", this.name);
        intent.putExtra("TIME", this.time);
        intent.putExtra("SOUND", this.sound);
        intent.putExtra("VIBRATE", this.vibrate);
        intent.putExtra("RECURRING", true);

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, this.id, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        List<Integer> time = DataHandler.getIntsFromString(this.time);
        ZonedDateTime date = ZonedDateTime.now()
                .withHour(time.get(0))
                .withMinute(time.get(1))
                .truncatedTo(ChronoUnit.MINUTES);

        int today = ZonedDateTime.now().getDayOfWeek().getValue() - 1;
        int nextDay;

        // Try and find a recurring day after today.
        // If there isn't one, there must be one up to and including today
        if (this.days.substring(today + 1).contains("1")) {
            nextDay = today + 1 + this.days.substring(today + 1).indexOf("1");
        } else {
            nextDay = this.days.indexOf("1");
        }

        ZonedDateTime day = date.minusDays(today).plusDays(nextDay);

        // If alarm time has already passed, increment week by 1
        if (day.toInstant().toEpochMilli() <= System.currentTimeMillis()) {
            day = day.plusDays(7);
        }

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                day.toInstant().toEpochMilli(),
                alarmPendingIntent
        );

        System.out.println(day.toInstant().toEpochMilli());

        this.isOn = 1;
    }

    public void cancel(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiverService.class);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, this.id, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(alarmPendingIntent);
        this.isOn = 0;
    }
}
