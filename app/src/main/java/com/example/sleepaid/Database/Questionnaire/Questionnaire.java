package com.example.sleepaid.Database.Questionnaire;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Questionnaire {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "questionnaireId")
    public int id;

    @NonNull
    public String name;

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}
