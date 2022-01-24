package com.example.sleepaid;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Question {
    @PrimaryKey
    @ColumnInfo(name = "questionId")
    public int id;

    public String question;
    public String information;
}
