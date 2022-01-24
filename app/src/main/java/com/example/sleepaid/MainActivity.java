package com.example.sleepaid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DBHelper db = new DBHelper(this);
        db.getWritableDatabase();

        Cursor answerData = db.load(SleepAidContract.SleepAidEntry.ANSWER_TABLE);

        if (answerData.moveToFirst()) {
            goToHomeScreen();
        }
        else {
            setContentView(R.layout.activity_main);
        }
    }

    public void startQuestionnaire(View view) {
        Intent questionnaire = new Intent(this, Questionnaire.class);
        startActivity(questionnaire);
    }

    private void goToHomeScreen() {
        Intent homeScreen = new Intent(this, HomeScreen.class);
        startActivity(homeScreen);
    }
}