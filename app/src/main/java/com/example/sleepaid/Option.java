package com.example.sleepaid;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Question.class,
                parentColumns = "questionId",
                childColumns = "questionId",
                onDelete = ForeignKey.CASCADE
        )
})
public class Option {
    @PrimaryKey
    @ColumnInfo(name = "optionId")
    public int id;

    public String value;
    public int questionId;
}
