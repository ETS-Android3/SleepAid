package com.example.sleepaid;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Option.class,
                parentColumns = "optionId",
                childColumns = "optionId",
                onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
                entity = Question.class,
                parentColumns = "questionId",
                childColumns = "questionId",
                onDelete = ForeignKey.CASCADE
        )
})
public class Answer {
    @PrimaryKey
    @ColumnInfo(name = "answerId")
    public int id;

    public int optionId;
    public int questionId;
}
