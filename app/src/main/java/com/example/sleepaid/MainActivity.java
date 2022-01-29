package com.example.sleepaid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());

        db.answerDao()
                .getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        answers -> {
                            if (!answers.isEmpty()) {
                                goToSleepDataScreen();
                            }
                            else {
                                setContentView(R.layout.activity_main);
                            }
                        },
                        Throwable::printStackTrace
                );
    }

    public void startQuestionnaire(View view) {
        Intent questionnaire = new Intent(this, Questionnaire.class);
        startActivity(questionnaire);
    }

    private void goToSleepDataScreen() {
        Intent sleepDataScreen = new Intent(this, SleepDataScreen.class);
        startActivity(sleepDataScreen);
    }
}
