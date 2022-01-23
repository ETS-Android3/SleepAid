package com.example.sleepaid;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Questionnaire extends AppCompatActivity {
    private DBHelper myDB;

    private int maxQuestions;

    private int currentQuestion;
    private int[] currentAnswers;

    private String[] questions;
    private String[] information;
    private ArrayList<Map<Integer, String>> options;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);

        myDB = new DBHelper(this);

        Cursor questionData = myDB.loadMax(
                SleepAidContract.SleepAidEntry.QUESTION_ID,
                SleepAidContract.SleepAidEntry.QUESTION_TABLE
        );
        maxQuestions = questionData.moveToFirst() ? questionData.getInt(0) : 0;

        currentAnswers = new int[maxQuestions];
        currentQuestion = 1;

        questions = new String[maxQuestions];
        information = new String[maxQuestions];
        options = new ArrayList(maxQuestions);

        loadAllQuestions();
        loadAllOptions();

        loadScreen(1);
    }

    @Override
    public void onBackPressed() {
        if (currentQuestion == 1) {
            confirmExit();
        }
        else {
            loadScreen(currentQuestion - 1);
        }
    }

    private void confirmExit() {
        Context context = this;
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Are you sure you want to exit the questionnaire?");

        //@TODO fix the styling for the buttons in dark mode
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent mainActivity = new Intent(context, MainActivity.class);
                startActivity(mainActivity);
            }
        });

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {}
                });

        alert.show();
    }

    private void loadAllQuestions() {
        String sortOrder = SleepAidContract.SleepAidEntry.QUESTION_ID + " ASC";

        Cursor questionData = myDB.load(
                SleepAidContract.SleepAidEntry.QUESTION_TABLE,
                null,
                null,
                null,
                sortOrder
        );

        int questionIdIndex = questionData.getColumnIndexOrThrow(SleepAidContract.SleepAidEntry.QUESTION_ID);
        int questionIndex = questionData.getColumnIndexOrThrow(SleepAidContract.SleepAidEntry.QUESTION_QUESTION);
        int informationIndex = questionData.getColumnIndexOrThrow(SleepAidContract.SleepAidEntry.QUESTION_INFORMATION);

        while(questionData.moveToNext()) {
            int questionId = questionData.getInt(questionIdIndex);

            questions[questionId - 1] = questionData.getString(questionIndex);
            information[questionId - 1] = questionData.getString(informationIndex);
        }
    }

    private void loadAllOptions() {
        String sortOrder = SleepAidContract.SleepAidEntry.OPTION_ID + " ASC";

        Cursor optionData = myDB.load(
                SleepAidContract.SleepAidEntry.OPTION_TABLE,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                sortOrder
        );

        int previousQuestionId = 0;
        Map optionMap = new HashMap<Integer, String>();

        int questionIdIndex = optionData.getColumnIndexOrThrow(SleepAidContract.SleepAidEntry.OPTION_QUESTION_ID);
        int optionIdIndex = optionData.getColumnIndexOrThrow(SleepAidContract.SleepAidEntry.OPTION_ID);
        int valueIndex = optionData.getColumnIndexOrThrow(SleepAidContract.SleepAidEntry.OPTION_VALUE);

        while(optionData.moveToNext()) {
            int questionId = optionData.getInt(questionIdIndex);
            int optionId = optionData.getInt(optionIdIndex);

            if (questionId != previousQuestionId) {
                previousQuestionId = questionId;

                optionMap = new HashMap<Integer, String>();
                optionMap.put(optionId, optionData.getString(valueIndex));

                options.add(questionId - 1, optionMap);
            }
            else {
                optionMap.put(optionId, optionData.getString(valueIndex));
                options.set(questionId - 1, optionMap);
            }
        }
    }

    public void loadNextQuestion(View view) {
        loadScreen(currentQuestion + 1);
    }

    public void loadPreviousQuestion(View view) {
        loadScreen(currentQuestion - 1);
    }

    private void loadScreen(int questionId) {
        if (currentQuestion <= maxQuestions) {
            RadioGroup radioGroup = findViewById(R.id.radioGroup);
            int checkedId = radioGroup.getCheckedRadioButtonId();
            currentAnswers[currentQuestion - 1] = checkedId == -1 ? 0 : checkedId;
        }

        if (questionId == 0) {
            confirmExit();
        }
        else if (questionId == maxQuestions + 1) {
            currentQuestion = questionId;

            setContentView(R.layout.activity_questionnaire_summary);
            loadAllAnswers();
        }
        else {
            if (currentQuestion > maxQuestions) {
                setContentView(R.layout.activity_questionnaire);
            }

            findViewById(R.id.scrollView).scrollTo(0, 0);

            currentQuestion = questionId;

            loadQuestion(questionId);
            loadOptions(questionId);
            loadPreviousAnswer(questionId);
        }
    }

    private void loadQuestion(int questionId) {
        TextBox questionBox = findViewById(R.id.question);
        questionBox.setText(questions[questionId - 1]);

        TextBox informationBox = findViewById(R.id.information);
        informationBox.setText(information[questionId - 1]);
    }

    private void loadOptions(int questionId) {
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.clearCheck();
        radioGroup.removeAllViews();

        Map optionData = options.get(questionId - 1);

        for (Object i : optionData.keySet()) {
            AppCompatRadioButton optionBox = new AppCompatRadioButton(this);
            optionBox.setId((int) i);

            optionBox.setText(optionData.get(i).toString());
            optionBox.setTextSize(20);
            optionBox.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.MATCH_PARENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT
            );

            int marginInDp = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    25,
                    getResources().getDisplayMetrics()
            );

            layoutParams.setMargins(0, 0, 0, marginInDp);
            optionBox.setLayoutParams(layoutParams);

            optionBox.setPadding(
                    marginInDp / 2,
                    marginInDp / 2,
                    marginInDp / 2,
                    marginInDp / 2
            );
            
            radioGroup.addView(optionBox);
        }
    }

    private void loadPreviousAnswer(int questionId) {
        if(currentAnswers[questionId - 1] != 0) {
            AppCompatRadioButton option = findViewById(currentAnswers[questionId - 1]);
            option.setChecked(true);
        }
    }

    private void loadAllAnswers() {
        LinearLayout layout = findViewById(R.id.answers);

        int sizeInDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                8,
                getResources().getDisplayMetrics()
        );

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, sizeInDp * 2);

        for (int i = 0 ; i < maxQuestions; i++) {
            TextBox textBox = new TextBox(this);
            textBox.setTextAllignment(View.TEXT_ALIGNMENT_TEXT_START);
            textBox.setTextSize(sizeInDp);
            textBox.setLayoutParams(layoutParams);

            String question = (i + 1) + ". " + questions[i];
            String answer = currentAnswers[i] != 0 ?
                    "A: " + options.get(i).get(currentAnswers[i]) :
                    "No answer";

            textBox.setText(question + "\n" + answer);

            layout.addView(textBox);
        }
    }

    public void storeAnswers(View view) {
        for (int i = 0; i < maxQuestions; i++) {
            myDB.add(
                    SleepAidContract.SleepAidEntry.ANSWER_OPTION_ID,
                    currentAnswers[i],
                    SleepAidContract.SleepAidEntry.ANSWER_TABLE
            );

            myDB.add(
                    SleepAidContract.SleepAidEntry.ANSWER_QUESTION_ID,
                    i + 1,
                    SleepAidContract.SleepAidEntry.ANSWER_TABLE
            );
        }

        Intent homeScreen = new Intent(this, HomeScreen.class);
        startActivity(homeScreen);
    }
}
