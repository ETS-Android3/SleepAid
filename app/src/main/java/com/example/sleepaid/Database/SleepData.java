package com.example.sleepaid.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = SleepDataField.class,
                parentColumns = "field",
                childColumns = "field",
                onDelete = ForeignKey.CASCADE
        )
})
public class SleepData {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "fieldId")
    public int id;

    public String field;
    public String date;
    public String value;

    public SleepData(String field, String date, String value) {
        this.field = field;
        this.date = date;
        this.value = value;
    }

    public int getId() {
        return this.id;
    }

    public String getField() {
        return this.field;
    }

    public String getDate() {
        return this.date;
    }

    public String getValue() {
        return this.value;
    }
}
