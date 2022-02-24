package com.example.sleepaid.Database.AlarmType;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {
        @Index(
                value = {"type"},
                unique = true
        )
})
public class AlarmType {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "typeId")
    public int id;

    @NonNull
    public String type;

    public AlarmType(String type) {
        this.type = type;
    }

    public int getId() {
        return this.id;
    }

    public String getType() {
        return this.type;
    }
}
