package com.example.sleepaid;

import android.provider.BaseColumns;

public final class SleepAidContract {
    private SleepAidContract() {}

    public static class SleepAidEntry implements BaseColumns {
        public static final String QUESTION_TABLE = "Question";
        public static final String QUESTION_ID = "QuestionId";
        public static final String QUESTION_QUESTION = "Question";
        public static final String QUESTION_INFORMATION = "Information";

        public static final String OPTION_TABLE = "Option";
        public static final String OPTION_ID = "OptionId";
        public static final String OPTION_VALUE = "Value";
        public static final String OPTION_QUESTION_ID = "QuestionId";

        public static final String ANSWER_TABLE = "Answer";
        public static final String ANSWER_ID = "AnswerId";
        public static final String ANSWER_OPTION_ID = "OptionId";
        public static final String ANSWER_QUESTION_ID = "QuestionId";
    }

    protected static final String CREATE_QUESTION_TABLE =
            "CREATE TABLE " +
                    SleepAidEntry.QUESTION_TABLE + " (" +
                    SleepAidEntry.QUESTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SleepAidEntry.QUESTION_QUESTION + " TEXT NOT NULL, " +
                    SleepAidEntry.QUESTION_INFORMATION + " TEXT NOT NULL)";

    protected static final String FILL_QUESTION_TABLE =
            "INSERT INTO " +
                    SleepAidEntry.QUESTION_TABLE + " (" +
                    SleepAidEntry.QUESTION_QUESTION + ", " +
                    SleepAidEntry.QUESTION_INFORMATION + ") VALUES " +
                    "('What time would you ideally like to go to sleep?'," +
                    " 'Ideally, people ought to go to bed earlier and wake up in the early morning hours. This pattern matches our biological tendencies to adapt our sleep pattern with that of the sun. You might find that you’re naturally sleepier after sundown.'), " +
                    "('What time would you ideally like to wake up in the morning?'," +
                    " 'The CDC recommends we get between 7 and 9 hours of sleep for a healthy life.'), " +
                    "('Do you tend to take naps?'," +
                    " 'The Sleep Foundation states that the best nap length is between 10 and 20 minutes, long enough to be refreshing but not so long that sleep inertia occurs.')";

    protected static final String DELETE_QUESTION_TABLE =
            "DROP TABLE IF EXISTS " + SleepAidEntry.QUESTION_TABLE;

    protected static final String CREATE_OPTION_TABLE =
            "CREATE TABLE " +
                    SleepAidEntry.OPTION_TABLE + " (" +
                    SleepAidEntry.OPTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SleepAidEntry.OPTION_VALUE + " TEXT NOT NULL, " +
                    SleepAidEntry.OPTION_QUESTION_ID + " INTEGER NOT NULL, " +
                    " FOREIGN KEY (" + SleepAidEntry.OPTION_QUESTION_ID + ") REFERENCES " + SleepAidEntry.QUESTION_TABLE + " (" + SleepAidEntry.QUESTION_ID + "))";

    protected static final String FILL_OPTION_TABLE =
            "INSERT INTO " +
                    SleepAidEntry.OPTION_TABLE + " (" +
                    SleepAidEntry.OPTION_VALUE + ", " +
                    SleepAidEntry.OPTION_QUESTION_ID + ") VALUES " +
                    "('Between 8 pm and 9 pm.', 1), " +
                    "('Between 9 pm and 10 pm.', 1), " +
                    "('Between 10 pm and 11 pm.', 1), " +
                    "('Between 11 pm and 12 am.', 1), " +
                    "('Between 12 am and 1 am.', 1), " +
                    "('Between 5 am and 6 am.', 2), " +
                    "('Between 6 am and 7 am.', 2), " +
                    "('Between 7 am and 8 am.', 2), " +
                    "('Between 8 am and 9 am.', 2), " +
                    "('Between 9 am and 10 am.', 2), " +
                    "('Yes.', 3), " +
                    "('No.', 3)";

    protected static final String DELETE_OPTION_TABLE =
            "DROP TABLE IF EXISTS " + SleepAidEntry.OPTION_TABLE;

    protected static final String CREATE_ANSWER_TABLE =
            "CREATE TABLE " +
                    SleepAidEntry.ANSWER_TABLE + " (" +
                    SleepAidEntry.ANSWER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SleepAidEntry.ANSWER_OPTION_ID + " INTEGER NOT NULL, " +
                    SleepAidEntry.ANSWER_QUESTION_ID + " INTEGER NOT NULL, " +
                    " FOREIGN KEY (" + SleepAidEntry.ANSWER_OPTION_ID + ") REFERENCES " + SleepAidEntry.OPTION_TABLE + " (" + SleepAidEntry.OPTION_ID + "), " +
                    " FOREIGN KEY (" + SleepAidEntry.ANSWER_QUESTION_ID + ") REFERENCES " + SleepAidEntry.QUESTION_TABLE + " (" + SleepAidEntry.QUESTION_ID + "))";

    protected static final String DELETE_ANSWER_TABLE =
            "DROP TABLE IF EXISTS " + SleepAidEntry.ANSWER_TABLE;
}
