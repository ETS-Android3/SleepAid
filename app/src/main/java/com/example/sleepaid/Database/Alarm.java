package com.example.sleepaid.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = AlarmType.class,
                parentColumns = "typeId",
                childColumns = "type",
                onDelete = ForeignKey.CASCADE
        )
})
public class Alarm {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "alarmId")
    public int id;

    public int type;
    public String time;
    public String days;
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
}
