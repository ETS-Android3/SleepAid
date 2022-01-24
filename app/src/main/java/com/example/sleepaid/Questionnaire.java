package com.example.sleepaid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;

import java.util.Arrays;

public class Questionnaire extends AppCompatActivity {
    private DBHelper myDB;

    private int maxQuestions;

    private int currentQuestion;
    private int[] currentAnswers;

    private String[] questions;
    private String[] information;
    private int[][] optionIds;
    private String[][] optionValues;

    private int sizeInDp;

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
        optionIds = new int[maxQuestions][];
        optionValues = new String[maxQuestions][];

        sizeInDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                25,
                getResources().getDisplayMetrics()
        );

        loadAllQuestions();
        loadAllOptions();

        loadScreen(1);
    }

    @Override
    public void onBackPressed() {
        if (currentQuestion == 1) {
            exitQuestionnaire();
        }
        else {
            loadScreen(currentQuestion - 1);
        }
    }

    private void exitQuestionnaire() {
        Context context = this;

        DialogInterface.OnClickListener exitAction = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent mainActivity = new Intent(context, MainActivity.class);
                startActivity(mainActivity);
            }
        };

        DialogInterface.OnClickListener cancelAction = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {}
        };

        Modal.show(
                context,
                getString(R.string.exit_questionnaire),
                getString(R.string.yes_modal),
                exitAction,
                getString(R.string.cancel_modal),
                cancelAction
        );
    }

    private void loadAllQuestions() {
        String sortOrder = SleepAidContract.SleepAidEntry.QUESTION_ID;

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
        String selection = SleepAidContract.SleepAidEntry.OPTION_QUESTION_ID + " = ?";
        String sortOrder = SleepAidContract.SleepAidEntry.OPTION_ID;

        for (int i = 0; i < maxQuestions; i++) {
            String[] selectionArgs = {Integer.toString(i + 1)};

            Cursor optionData = myDB.load(
                    SleepAidContract.SleepAidEntry.OPTION_TABLE,   // The table to query
                    null,             // The array of columns to return (pass null to get all)
                    selection,              // The columns for the WHERE clause
                    selectionArgs,          // The values for the WHERE clause
                    sortOrder
            );

            int optionIdIndex = optionData.getColumnIndexOrThrow(SleepAidContract.SleepAidEntry.OPTION_ID);
            int valueIndex = optionData.getColumnIndexOrThrow(SleepAidContract.SleepAidEntry.OPTION_VALUE);

            int maxOptions = optionData.getCount();
            optionIds[i] = new int[maxOptions];
            optionValues[i] = new String[maxOptions];

            int j = 0;

            while(optionData.moveToNext()) {
                optionIds[i][j] = optionData.getInt(optionIdIndex);
                optionValues[i][j] = optionData.getString(valueIndex);

                j++;
            }
        }
    }

    public void loadNextQuestion(View view) {
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        int checkedId = radioGroup.getCheckedRadioButtonId();

        if (checkedId == -1) {
            DialogInterface.OnClickListener cancelAction = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {}
            };

            Modal.show(
                    this,
                    getString(R.string.question_validation),
                    getString(R.string.ok_modal),
                    cancelAction,
                    null,
                    null
            );
        }
        else {
            loadScreen(currentQuestion + 1);
        }
    }

    public void loadPreviousQuestion(View view) {
        if (currentQuestion > maxQuestions) {
            setContentView(R.layout.activity_questionnaire);
        }

        loadScreen(currentQuestion - 1);
    }

    private void loadScreen(int questionId) {
        if (currentQuestion <= maxQuestions) {
            RadioGroup radioGroup = findViewById(R.id.radioGroup);
            int checkedId = radioGroup.getCheckedRadioButtonId();
            currentAnswers[currentQuestion - 1] = checkedId == -1 ? 0 : checkedId;
        }

        if (questionId == 0) {
            exitQuestionnaire();
        }
        else if (questionId == maxQuestions + 1) {
            currentQuestion = questionId;

            setContentView(R.layout.activity_questionnaire_summary);
            loadAllAnswers();
        }
        else {
            findViewById(R.id.scrollView).scrollTo(0, 0);

            currentQuestion = questionId;

            loadQuestion(questionId);
            loadOptions(questionId);
            loadPreviousAnswer(questionId);

            if (questionId == 2) {
                presetWakeUpTime(questionId);
            }
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

        for (int i = 0; i < optionIds[questionId - 1].length; i++) {
            AppCompatRadioButton optionBox = new AppCompatRadioButton(this);
            optionBox.setId(optionIds[questionId - 1][i]);

            optionBox.setText(optionValues[questionId - 1][i]);
            optionBox.setTextSize((int) (sizeInDp / 3.5));
            optionBox.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.MATCH_PARENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT
            );

            layoutParams.setMargins(0, 0, 0, sizeInDp);
            optionBox.setLayoutParams(layoutParams);

            optionBox.setPadding(
                    sizeInDp / 2,
                    sizeInDp / 2,
                    sizeInDp / 2,
                    sizeInDp / 2
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

    private void presetWakeUpTime(int questionId) {
        int previousAnswer = currentAnswers[questionId - 2];
        int firstOption = optionIds[questionId - 1][0];

        AppCompatRadioButton option = findViewById(previousAnswer + firstOption - 1);

        TextBox information = findViewById(R.id.information);
        String currentText = information.getText().toString();
        String newText = getString(R.string.wakeup_time) + option.getText().toString().toLowerCase();

        information.setText(currentText + "\n " + newText);

        if(currentAnswers[questionId - 1] == 0) {
            option.setChecked(true);
        }
    }

    private void loadAllAnswers() {
        LinearLayout layout = findViewById(R.id.answers);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, sizeInDp / 2);

        for (int i = 0 ; i < maxQuestions; i++) {
            TextBox textBox = new TextBox(this);

            textBox.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            textBox.setTextSize((int) (sizeInDp / 3.5));
            textBox.setLayoutParams(layoutParams);

            String question = (i + 1) + ". " + questions[i];
            String answer = currentAnswers[i] != 0 ?
                    "A: " + optionValues[i][Arrays.binarySearch(optionIds[i], currentAnswers[i])] :
                    "No answer";

            textBox.setText(question + "\n" + answer);

            layout.addView(textBox);
        }
    }

    public void storeAnswers(View view) {
        for (int i = 0; i < maxQuestions; i++) {
            String[] columns = {
                    SleepAidContract.SleepAidEntry.ANSWER_OPTION_ID,
                    SleepAidContract.SleepAidEntry.ANSWER_QUESTION_ID
            };

            int[] values = {
                    currentAnswers[i],
                    i + 1
            };

            myDB.add(
                    columns,
                    values,
                    SleepAidContract.SleepAidEntry.ANSWER_TABLE
            );
        }

        Intent homeScreen = new Intent(this, HomeScreen.class);
        startActivity(homeScreen);
    }
}
