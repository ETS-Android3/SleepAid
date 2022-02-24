package com.example.sleepaid.Database.SleepData;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.example.sleepaid.Database.SleepDataField.SleepDataField;

@Entity(primaryKeys = {
        "field",
        "date"
},
        foreignKeys = {
        @ForeignKey(
                entity = SleepDataField.class,
                parentColumns = "name",
                childColumns = "field",
                onDelete = ForeignKey.CASCADE
        )
})
public class SleepData {
    @NonNull
    public String field;
    @NonNull
    public String date;
    @NonNull
    public String value;

    public SleepData(String field, String date, String value) {
        this.field = field;
        this.date = date;
        this.value = value;
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
