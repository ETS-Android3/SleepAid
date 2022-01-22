package com.example.sleepaid;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;

public class Questionnaire extends AppCompatActivity {
    private DBHelper myDB;
    private int currentQuestion;
    private int[] currentAnswers;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);

        myDB = new DBHelper(this);
        myDB.getWritableDatabase();

        currentAnswers = new int[10];

        loadScreen(1);
    }

    private void loadScreen(int questionId) {
        SQLiteDatabase db = myDB.getReadableDatabase();

        loadQuestion(db, questionId);
        loadOptions(db, questionId);
        loadPreviousAnswers(questionId);

        currentQuestion = questionId;
    }

    public void loadNextQuestion(View view) {
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        currentAnswers[currentQuestion] = radioGroup.getCheckedRadioButtonId();

        loadScreen(currentQuestion + 1);
    }

    public void loadPreviousQuestion(View view) {
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        currentAnswers[currentQuestion] = radioGroup.getCheckedRadioButtonId();

        loadScreen(currentQuestion - 1);
    }

    private void loadQuestion(SQLiteDatabase db, int questionId) {
        String selection = SleepAidContract.SleepAidEntry.QUESTION_ID + " = ?";
        String[] selectionArgs = {Integer.toString(questionId)};

        Cursor cursor = db.query(
                SleepAidContract.SleepAidEntry.QUESTION_TABLE,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,
                null,
                null
        );

        cursor.moveToFirst();
        String questionText = cursor.getString(cursor.getColumnIndexOrThrow(SleepAidContract.SleepAidEntry.QUESTION_QUESTION));
        String informationText = cursor.getString(cursor.getColumnIndexOrThrow(SleepAidContract.SleepAidEntry.QUESTION_INFORMATION));

        TextBox questionBox = findViewById(R.id.question);
        questionBox.setText(questionText);

        TextBox informationBox = findViewById(R.id.information);
        informationBox.setText(informationText);
    }

    private void loadOptions(SQLiteDatabase db, int questionId) {
        String selection = SleepAidContract.SleepAidEntry.OPTION_QUESTION_ID + " = ?";
        String[] selectionArgs = {Integer.toString(questionId)};

        Cursor cursor = db.query(
                SleepAidContract.SleepAidEntry.OPTION_TABLE,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,
                null,
                null
        );

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.removeAllViews();

        while(cursor.moveToNext()) {
            String optionText = cursor.getString(cursor.getColumnIndexOrThrow(SleepAidContract.SleepAidEntry.OPTION_VALUE));
            int optionId = cursor.getInt(cursor.getColumnIndexOrThrow(SleepAidContract.SleepAidEntry.OPTION_ID));

            AppCompatRadioButton optionBox = new AppCompatRadioButton(this);
            optionBox.setId(optionId);

            optionBox.setText(optionText);
            optionBox.setTextSize(20);
            optionBox.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 0, 50);
            optionBox.setLayoutParams(layoutParams);
            optionBox.setPadding(25, 25, 25, 25);
            
            radioGroup.addView(optionBox);
        }
    }

    private void loadPreviousAnswers(int questionId) {
        if(currentAnswers[questionId] != 0) {
            AppCompatRadioButton option = findViewById(currentAnswers[questionId]);
            option.setChecked(true);
        }
    }
}
