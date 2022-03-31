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
    public String information;
    public String copyright;

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getInformation() {
        return this.information;
    }

    public String getCopyright() {
        return this.copyright;
    }
}
