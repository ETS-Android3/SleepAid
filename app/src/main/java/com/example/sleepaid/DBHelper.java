package com.example.sleepaid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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

        db.execSQL(SleepAidContract.CREATE_OPTION_TABLE);
        db.execSQL(SleepAidContract.FILL_OPTION_TABLE);

        db.execSQL(SleepAidContract.CREATE_ANSWER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DELETE_DATABASE =  SleepAidContract.DELETE_QUESTION_TABLE +
                                 SleepAidContract.DELETE_OPTION_TABLE +
                                 SleepAidContract.DELETE_ANSWER_TABLE;

        db.execSQL(DELETE_DATABASE);
        onCreate(db);
    }

    public Cursor load(String table, String[] columns, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(
                table,   // The table to query
                columns,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,
                null,
                sortOrder
        );
    }

    public Cursor load(String table) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery("SELECT * FROM " + table, null);
    }

    public Cursor loadMax(String column, String table) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery("SELECT MAX(" + column + ") FROM " + table, null);
    }

    public void add(String column, int value, String table) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(column, value);

        db.insert(table, null, values);
    }

    public void add(String column, String value, String table) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(column, value);

        db.insert(table, null, values);
    }

    public int update(String column, int value, String selection, String[] selectionArgs, String table) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(column, value);

        return db.update(
                table,
                values,
                selection,
                selectionArgs
        );
    }

    public int update(String column, String value, String selection, String[] selectionArgs, String table) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(column, value);

        return db.update(
                table,
                values,
                selection,
                selectionArgs
        );
    }
}
