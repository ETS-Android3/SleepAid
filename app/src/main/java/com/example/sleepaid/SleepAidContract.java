package com.example.sleepaid;

import android.provider.BaseColumns;

public final class SleepAidContract {
    private SleepAidContract() {}

    public static class SleepAidEntry implements BaseColumns {
        public static final String QUESTION_TABLE = "Question";
        public static final String QUESTION_ID = "QuestionId";
        public static final String QUESTION_QUESTION = "Question";

        public static final String QUESTION_VALUE_TABLE = "QuestionValue";
        public static final String QUESTION_VALUE_ID = "QuestionValueId";
        public static final String QUESTION_VALUE_VALUE = "Value";
        public static final String QUESTION_VALUE_QUESTION_ID = "QuestionId";

        public static final String ANSWER_TABLE = "Answer";
        public static final String ANSWER_ID = "QuestionValueId";
        public static final String ANSWER_QUESTION_VALUE_ID = "QuestionValueId";
        public static final String ANSWER_QUESTION_ID = "QuestionId";
    }

    protected static final String CREATE_QUESTION_TABLE =
            "CREATE TABLE" + SleepAidEntry.QUESTION_TABLE + "(" +
            SleepAidEntry.QUESTION_ID + "INTEGER PRIMARY KEY AUTOINCREMENT," +
            SleepAidEntry.QUESTION_QUESTION + "TEXT NOT NULL);";
    protected static final String DELETE_QUESTION_TABLE = "DROP TABLE IF EXISTS " + SleepAidEntry.QUESTION_TABLE;

    protected static final String CREATE_QUESTION_VALUE_TABLE =
            "CREATE TABLE" + SleepAidEntry.QUESTION_VALUE_TABLE + "(" +
            SleepAidEntry.QUESTION_VALUE_ID + "INTEGER PRIMARY KEY AUTOINCREMENT," +
            SleepAidEntry.QUESTION_VALUE_VALUE + "TEXT NOT NULL" +
            SleepAidEntry.QUESTION_VALUE_QUESTION_ID + "INTEGER NOT NULL," +
            " FOREIGN KEY (" + SleepAidEntry.QUESTION_VALUE_QUESTION_ID + ") REFERENCES " + SleepAidEntry.QUESTION_TABLE + "(" + SleepAidEntry.QUESTION_ID + "));";
    protected static final String DELETE_QUESTION_VALUE_TABLE = "DROP TABLE IF EXISTS " + SleepAidEntry.QUESTION_VALUE_TABLE;

    protected static final String CREATE_ANSWER_TABLE =
            "CREATE TABLE" + SleepAidEntry.ANSWER_TABLE + "(" +
            SleepAidEntry.ANSWER_ID + "INTEGER PRIMARY KEY AUTOINCREMENT," +
            SleepAidEntry.ANSWER_QUESTION_VALUE_ID + "INTEGER NOT NULL," +
            SleepAidEntry.ANSWER_QUESTION_ID + "INTEGER NOT NULL," +
            " FOREIGN KEY (" + SleepAidEntry.ANSWER_QUESTION_VALUE_ID + ") REFERENCES " + SleepAidEntry.QUESTION_VALUE_TABLE + "(" + SleepAidEntry.QUESTION_VALUE_ID + "))," +
            " FOREIGN KEY (" + SleepAidEntry.ANSWER_QUESTION_ID + ") REFERENCES " + SleepAidEntry.QUESTION_TABLE + "(" + SleepAidEntry.QUESTION_ID + "));";
    protected static final String DELETE_ANSWER_TABLE = "DROP TABLE IF EXISTS " + SleepAidEntry.ANSWER_TABLE;
}
