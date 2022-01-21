package com.example.sleepaid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBHelper myDB = new DBHelper(this);
        myDB.getWritableDatabase();
    }

    public void startQuestionnaire(android.view.View view) {
        setContentView(R.layout.activity_question1);
    }
}