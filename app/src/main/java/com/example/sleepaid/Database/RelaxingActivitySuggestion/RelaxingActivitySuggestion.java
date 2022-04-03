package com.example.sleepaid.Database.RelaxingActivitySuggestion;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {
        @Index(
                value = {"name"},
                unique = true
        )
})
public class RelaxingActivitySuggestion {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "relaxingActivityId")
    public int id;

    @NonNull
    public String name;
    @NonNull
    public String information;

    public RelaxingActivitySuggestion(String name, String information) {
        this.name = name;
        this.information = information;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getInformation() {
        return this.information;
    }
}
