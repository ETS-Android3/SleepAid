package com.example.sleepaid.Database.Question;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Question {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "questionId")
    public int id;

    public String question;
    public String information;

    public int getId() {
        return this.id;
    }

    public String getQuestion() {
        return this.question;
    }

    public String getInformation() {
        return this.information;
    }
}
