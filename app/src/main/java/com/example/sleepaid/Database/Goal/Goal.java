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
    public String valueMin;
    @NonNull
    public String valueMax;

    public Goal(String name, String valueMin, String valueMax) {
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

    public String getValueMin() {
        return this.valueMin;
    }

    public String getValueMax() {
        return this.valueMax;
    }

    public void setValueMin(String valueMin) {
        this.valueMin = valueMin;
    }

    public void setValueMax(String valueMax) {
        this.valueMax = valueMax;
    }
}
