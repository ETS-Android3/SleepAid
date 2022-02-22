package com.example.sleepaid.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Goal {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "goalId")
    public int id;

    public String name;
    public String value;

    public Goal(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }
}
