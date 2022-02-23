package com.example.sleepaid.Database.SleepDataField;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {
        @Index(
                value = {"field"},
                unique = true
        )
})
public class SleepDataField {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "fieldId")
    public int id;

    public String field;

    public SleepDataField(String field) {
        this.field = field;
    }

    public int getId() {
        return this.id;
    }

    public String getField() {
        return this.field;
    }
}
