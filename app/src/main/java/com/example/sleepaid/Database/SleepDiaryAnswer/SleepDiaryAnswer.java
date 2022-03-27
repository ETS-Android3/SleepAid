package com.example.sleepaid.Database.SleepDiaryAnswer;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.sleepaid.Database.Option.Option;
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
public class SleepDiaryAnswer {
    @NonNull
    public String value;
    public int questionId;
    @NonNull
    public int section;
    @NonNull
    public String date;

    public SleepDiaryAnswer(String value, int questionId, int section, String date) {
        this.value = value;
        this.questionId = questionId;
        this.section = section;
        this.date = date;
    }

    public String getValue() {
        return this.value;
    }

    public int getQuestionId() {
        return this.questionId;
    }

    public int getSection() {
        return this.section;
    }

    public String getDate() {
        return this.date;
    }
}
