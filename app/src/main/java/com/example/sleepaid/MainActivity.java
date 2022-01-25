package com.example.sleepaid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "sleep-aid.db"
        ).createFromAsset("database/initial-data.db").build();

        db.answerDao().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        answers -> {
                            if (!answers.isEmpty()) {
                                goToHomeScreen();
                            }
                            else {
                                setContentView(R.layout.activity_main);
                            }
                        },
                        Throwable::printStackTrace
                );

//        DBHelper db = new DBHelper(this);
//        db.getWritableDatabase();
//
//        Cursor answerData = db.load(SleepAidContract.SleepAidEntry.ANSWER_TABLE);
//
//        if (answerData.moveToFirst()) {
//            goToHomeScreen();
//        }
//        else {
//            setContentView(R.layout.activity_main);
//        }
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
