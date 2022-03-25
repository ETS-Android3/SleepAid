package com.example.sleepaid.Database.Option;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.sleepaid.Database.Question.Question;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Question.class,
                parentColumns = "questionId",
                childColumns = "questionId",
                onDelete = ForeignKey.CASCADE
        )
})
public class Option implements Comparable<Option> {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "optionId")
    public int id;

    @NonNull
    public String value;
    public int questionId;

    public int getId() {
        return this.id;
    }

    public String getValue() {
        return this.value;
    }

    public int getQuestionId() {
        return this.questionId;
    }

    @Override
    public int compareTo(Option newOption) {
        return this.questionId - newOption.getQuestionId();
    }
}
