package com.example.sleepaid.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sleepaid.App;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Listener.OnSwipeTouchListener;
import com.example.sleepaid.R;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class QuestionnaireScreen extends AppCompatActivity {
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
                                goToMainMenuScreen();
                            }
                            else {
                                setContentView(R.layout.activity_questionnaire_screen_host);
                            }
                        },
                        Throwable::printStackTrace
                );
    }

    private void goToMainMenuScreen() {
        Intent mainMenuScreen = new Intent(this, MainMenuScreen.class);
        startActivity(mainMenuScreen);
    }
}
