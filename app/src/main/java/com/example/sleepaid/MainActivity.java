package com.example.sleepaid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
        Intent questionnaire = new Intent(this, Questionnaire.class);
        startActivity(questionnaire);
    }
}