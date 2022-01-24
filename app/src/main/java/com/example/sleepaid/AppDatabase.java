package com.example.sleepaid;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {
        Question.class,
        Option.class,
        Answer.class
}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract QuestionDao questionDao();

    public abstract OptionDao optionDao();

    public abstract AnswerDao answerDao();
}
