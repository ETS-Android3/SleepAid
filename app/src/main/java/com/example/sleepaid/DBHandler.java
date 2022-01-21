package com.example.sleepaid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SleepAid.db";

    //initialize the database
    public DBHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DATABASE = SleepAidContract.CREATE_QUESTION_TABLE +
                                 SleepAidContract.CREATE_QUESTION_VALUE_TABLE +
                                 SleepAidContract.CREATE_ANSWER_TABLE;

        db.execSQL(CREATE_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DELETE_ENTRIES =  SleepAidContract.DELETE_QUESTION_TABLE +
                                 SleepAidContract.DELETE_QUESTION_VALUE_TABLE +
                                 SleepAidContract.DELETE_ANSWER_TABLE;

        db.execSQL(DELETE_ENTRIES);
        onCreate(db);
    }

    //public String loadHandler() {}
    //public void addHandler(Student student) {}
    //public Student findHandler(String studentname) {}
    //public boolean deleteHandler(int ID) {}
    //public boolean updateHandler(int ID, String name) {}
}
