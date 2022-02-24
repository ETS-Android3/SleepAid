package com.example.sleepaid.Database.Goal;

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
public class Goal {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "goalId")
    public int id;

    @NonNull
    public String name;
    @NonNull
    public int valueMin;
    @NonNull
    public int valueMax;

    public Goal(String name, int valueMin, int valueMax) {
        this.name = name;
        this.valueMin = valueMin;
        this.valueMax = valueMax;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getValueMin() {
        return this.valueMin;
    }

    public int getValueMax() {
        return this.valueMax;
    }
}
