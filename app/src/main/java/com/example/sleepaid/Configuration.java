package com.example.sleepaid;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Configuration {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "configurationId")
    public int id;

    public String type;
    public String value;

    public Configuration(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public int getId() {
        return this.id;
    }

    public String getType() {
        return this.type;
    }

    public String getValue() {
        return this.value;
    }
}
