package com.example.sleepaid.Database.Question;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.sleepaid.Database.Questionnaire.Questionnaire;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Questionnaire.class,
                parentColumns = "questionnaireId",
                childColumns = "questionnaireId",
                onDelete = ForeignKey.CASCADE
        )
})
public class Question {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "questionId")
    public int id;

    @NonNull
    public String question;

    @NonNull
    public String information;

    public int questionnaireId;

    public int getId() {
        return this.id;
    }

    public String getQuestion() {
        return this.question;
    }

    public String getInformation() {
        return this.information;
    }

    public int getQuestionnaireId() {
        return this.questionnaireId;
    }
}
