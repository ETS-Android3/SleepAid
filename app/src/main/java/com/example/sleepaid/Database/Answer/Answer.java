package com.example.sleepaid.Database.Answer;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.sleepaid.Database.Option.Option;
import com.example.sleepaid.Database.Question.Question;

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
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "answerId")
    public int id;

    public int optionId;
    public int questionId;

    public Answer(int optionId, int questionId) {
        this.optionId = optionId;
        this.questionId = questionId;
    }

    public int getId() {
        return this.id;
    }

    public int getOptionId() {
        return this.optionId;
    }

    public int getQuestionId() {
        return this.questionId;
    }
}
