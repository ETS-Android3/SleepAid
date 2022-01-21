package com.example.sleepaid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SleepAid.db";

    //initialize the database
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SleepAidContract.CREATE_QUESTION_TABLE);
        db.execSQL(SleepAidContract.FILL_QUESTION_TABLE);

        db.execSQL(SleepAidContract.CREATE_QUESTION_VALUE_TABLE);
        //db.execSQL(SleepAidContract.FILL_QUESTION_VALUE_TABLE);

        db.execSQL(SleepAidContract.CREATE_ANSWER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DELETE_DATABASE =  SleepAidContract.DELETE_QUESTION_TABLE +
                                 SleepAidContract.DELETE_QUESTION_VALUE_TABLE +
                                 SleepAidContract.DELETE_ANSWER_TABLE;

        db.execSQL(DELETE_DATABASE);
        onCreate(db);
    }

    //public String loadHandler() {}
    //public void addHandler(Student student) {}
    //public Student findHandler(String studentname) {}
    //public boolean deleteHandler(int ID) {}
    //public boolean updateHandler(int ID, String name) {}
}
