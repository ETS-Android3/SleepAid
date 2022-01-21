package com.example.sleepaid;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    DBHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDB = new DBHelper(this);
        myDB.getWritableDatabase();
    }

    public void startQuestionnaire(View view) {
        SQLiteDatabase db = myDB.getReadableDatabase();

        String[] projection = {
                SleepAidContract.SleepAidEntry.QUESTION_ID,
                SleepAidContract.SleepAidEntry.QUESTION_QUESTION
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = SleepAidContract.SleepAidEntry.QUESTION_ID + " = ?";
        String[] selectionArgs = {"1"};

        Cursor cursor = db.query(
                SleepAidContract.SleepAidEntry.QUESTION_TABLE,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,
                null,
                null
        );
        String question1 = cursor.getString(1);
        Log.i("question", question1);
        setContentView(R.layout.activity_question1);
        InformationBox question = (InformationBox) findViewById(R.id.informationBox2);
        //question.setText(question1);
    }
}