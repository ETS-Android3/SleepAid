package com.example.sleepaid.Database.Alarm;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.sleepaid.DataHandler;
import com.example.sleepaid.Database.AlarmType.AlarmType;

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
    public String time;
    @NonNull
    public String days;
    @NonNull
    public String sound;

    public Alarm(int type, String time, String days, String sound) {
        this.type = type;
        this.time = time;
        this.days = days;
        this.sound = sound;
    }

    public int getId() {
        return this.id;
    }

    public int getType() {
        return this.type;
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

    public void setTime(String time) {
        this.time = time;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public void setSound(String sound) {
        this.sound = sound;
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
}
