package com.example.sleepaid.Database;

import android.content.Context;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.sleepaid.Database.Alarm.Alarm;
import com.example.sleepaid.Database.Alarm.AlarmDao;
import com.example.sleepaid.Database.AlarmType.AlarmType;
import com.example.sleepaid.Database.AlarmType.AlarmTypeDao;
import com.example.sleepaid.Database.Answer.Answer;
import com.example.sleepaid.Database.Answer.AnswerDao;
import com.example.sleepaid.Database.Configuration.Configuration;
import com.example.sleepaid.Database.Configuration.ConfigurationDao;
import com.example.sleepaid.Database.Goal.Goal;
import com.example.sleepaid.Database.Goal.GoalDao;
import com.example.sleepaid.Database.Option.Option;
import com.example.sleepaid.Database.Option.OptionDao;
import com.example.sleepaid.Database.Question.Question;
import com.example.sleepaid.Database.Question.QuestionDao;
import com.example.sleepaid.Database.Questionnaire.Questionnaire;
import com.example.sleepaid.Database.Questionnaire.QuestionnaireDao;
import com.example.sleepaid.Database.SleepData.SleepData;
import com.example.sleepaid.Database.SleepData.SleepDataDao;
import com.example.sleepaid.Database.SleepDataField.SleepDataField;
import com.example.sleepaid.Database.SleepDataField.SleepDataFieldDao;

@Database(entities = {
        Questionnaire.class,
        Question.class,
        Option.class,
        Answer.class,
        Alarm.class,
        AlarmType.class,
        Configuration.class,
        Goal.class,
        SleepData.class,
        SleepDataField.class
},
        version = 1,
        exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;
    private static final String dbName = "sleep-aid.db";

    public abstract QuestionnaireDao questionnaireDao();

    public abstract QuestionDao questionDao();

    public abstract OptionDao optionDao();

    public abstract AnswerDao answerDao();

    public abstract AlarmDao alarmDao();

    public abstract AlarmTypeDao alarmTypeDao();

    public abstract ConfigurationDao configurationDao();

    public abstract GoalDao goalDao();

    public abstract SleepDataDao sleepDataDao();

    public abstract SleepDataFieldDao sleepDataFieldDao();

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context,
                            AppDatabase.class,
                            dbName
                    )
                            .createFromAsset("database/initial-data.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
