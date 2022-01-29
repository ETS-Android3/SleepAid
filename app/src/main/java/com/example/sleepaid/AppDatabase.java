package com.example.sleepaid;

import android.content.Context;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {
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
        autoMigrations = {
                @AutoMigration(from = 1, to = 4)
        },
        version = 4)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;
    private static final String dbName = "sleep-aid.db";

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
                    ).createFromAsset("database/initial-data.db").build();
                }
            }
        }
        return INSTANCE;
    }
}
