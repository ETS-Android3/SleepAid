package com.example.sleepaid.Database.Alarm;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.sleepaid.Service.AlarmBroadcastReceiverService;
import com.example.sleepaid.Handler.DataHandler;
import com.example.sleepaid.Database.AlarmType.AlarmType;

import java.util.Calendar;
import java.util.List;

@SuppressLint("NewApi")
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

    public void schedule(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmBroadcastReceiverService.class);

        intent.putExtra("ID", this.id);
        intent.putExtra("NAME", this.name == null ? "" : this.name);
        intent.putExtra("TIME", this.time);
        intent.putExtra("SOUND", this.sound);
        intent.putExtra("VIBRATE", this.vibrate);

        boolean recurring = this.days.contains("1");
        intent.putExtra("RECURRING", recurring);

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, this.id, intent, PendingIntent.FLAG_MUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        List<Integer> time = DataHandler.getIntsFromString(this.time);
        calendar.set(Calendar.HOUR_OF_DAY, time.get(0));
        calendar.set(Calendar.MINUTE, time.get(1));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // If alarm time has already passed and it's not a recurring alarm, increment day by 1
        if (calendar.getTimeInMillis() <= System.currentTimeMillis() && !recurring) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                alarmPendingIntent
        );

        this.isOn = 1;
    }

    public void scheduleRepeat(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmBroadcastReceiverService.class);

        intent.putExtra("ID", this.id);
        intent.putExtra("NAME", this.name == null ? "" : this.name);
        intent.putExtra("TIME", this.time);
        intent.putExtra("SOUND", this.sound);
        intent.putExtra("VIBRATE", this.vibrate);

        boolean recurring = this.days.contains("1");
        intent.putExtra("RECURRING", recurring);

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, this.id, intent, PendingIntent.FLAG_MUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        List<Integer> time = DataHandler.getIntsFromString(this.time);
        calendar.set(Calendar.HOUR_OF_DAY, time.get(0));
        calendar.set(Calendar.MINUTE, time.get(1));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;

        for (int i = 0; i < 7; i++) {
            if (this.days.charAt(i) == '1') {
                Calendar day = (Calendar) calendar.clone();
                day.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - (today - i));

                // If alarm time has already passed, increment week by 1
                if (day.getTimeInMillis() <= System.currentTimeMillis()) {
                    day.set(Calendar.DAY_OF_MONTH, day.get(Calendar.DAY_OF_MONTH) + 7);
                }

                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        day.getTimeInMillis(),
                        alarmPendingIntent
                );
            }
        }
    }

    public void cancel(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiverService.class);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, this.id, intent, PendingIntent.FLAG_MUTABLE);
        alarmManager.cancel(alarmPendingIntent);
        this.isOn = 0;
    }
}
