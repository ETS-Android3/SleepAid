package com.example.sleepaid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    AppDatabase db;
    LoadAnswers runningTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "sleep-aid.db"
        ).build();

        if (runningTask != null) {
            runningTask.cancel(true);
        }
        runningTask = new LoadAnswers();
        runningTask.execute();

//        myDB = new DBHelper(this);
//        myDB.getWritableDatabase();

//        Cursor answerData = myDB.load(SleepAidContract.SleepAidEntry.ANSWER_TABLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel running task(s) to avoid memory leaks
        if (runningTask != null)
            runningTask.cancel(true);
    }

    public void startQuestionnaire(View view) {
        Intent questionnaire = new Intent(this, Questionnaire.class);
        startActivity(questionnaire);
    }

    private void goToHomeScreen() {
        Intent homeScreen = new Intent(this, HomeScreen.class);
        startActivity(homeScreen);
    }

    private final class LoadAnswers extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            return db.answerDao().getAll().isEmpty() ? "Yes" : "No";
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == "Yes") {
                setContentView(R.layout.activity_main);
            }
            else {
                goToHomeScreen();
            }
        }
    }
}