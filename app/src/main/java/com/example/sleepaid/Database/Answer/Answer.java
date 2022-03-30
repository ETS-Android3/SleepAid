package com.example.sleepaid.Database.Answer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.example.sleepaid.Database.Question.Question;

@Entity(primaryKeys = {
        "questionId",
        "section",
        "date"
},
        foreignKeys = {
        @ForeignKey(
                entity = Question.class,
                parentColumns = "questionId",
                childColumns = "questionId",
                onDelete = ForeignKey.CASCADE
        )
})
public class Answer {
    @NonNull
    public String value;
    public int questionId;

    public Integer optionId;

    @NonNull
    public int section;
    @NonNull
    public String date;

    public Answer(String value, int questionId, Integer optionId, int section, String date) {
        this.value = value;
        this.questionId = questionId;
        this.optionId = optionId;
        this.section = section;
        this.date = date;
    }

    public String getValue() {
        return this.value;
    }

    public int getQuestionId() {
        return this.questionId;
    }

    public Integer getOptionId() {
        return this.optionId;
    }

    public int getSection() {
        return this.section;
    }

    public String getDate() {
        return this.date;
    }
}
