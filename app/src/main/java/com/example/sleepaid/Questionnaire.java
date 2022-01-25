package com.example.sleepaid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@SuppressLint("NewApi")
public class Questionnaire extends AppCompatActivity {
    AppDatabase db;

    private int currentQuestionId;
    private List<Answer> currentAnswers;

    private List<Question> questions;
    private List<Option> options;

    private int sizeInDp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);

        db = AppDatabase.getDatabase(getApplicationContext());

        sizeInDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                25,
                getResources().getDisplayMetrics()
        );

        currentQuestionId = 1;

        loadAllQuestions();
        loadAllOptions();

        loadScreen(1);
    }

    @Override
    public void onBackPressed() {
        if (currentQuestionId == 1) {
            exitQuestionnaire();
        }
        else {
            loadScreen(currentQuestionId - 1);
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
        db.questionDao()
                .getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        questionData -> questions = questionData,
                        Throwable::printStackTrace
                );
    }

    private void loadAllOptions() {
        db.optionDao()
                .getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        optionData -> options = optionData,
                        Throwable::printStackTrace
                );
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
            loadScreen(currentQuestionId + 1);
        }
    }

    public void loadPreviousQuestion(View view) {
        if (currentQuestionId > questions.size()) {
            setContentView(R.layout.activity_questionnaire);
        }

        loadScreen(currentQuestionId - 1);
    }

    private void loadScreen(int questionId) {
        if (currentQuestionId <= questions.size()) {
            RadioGroup radioGroup = findViewById(R.id.radioGroup);
            int checkedId = radioGroup.getCheckedRadioButtonId();

            if (checkedId != -1) {
                currentAnswers.add(new Answer(checkedId, currentQuestionId));
            }
        }

        if (questionId == 0) {
            exitQuestionnaire();
        }
        else if (questionId == questions.size() + 1) {
            currentQuestionId = questionId;

            setContentView(R.layout.activity_questionnaire_summary);
            loadAllAnswers();
        }
        else {
            findViewById(R.id.scrollView).scrollTo(0, 0);

            currentQuestionId = questionId;

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
        TextBox informationBox = findViewById(R.id.information);

        Optional<Question> question = questions
                .stream()
                .filter(q -> q.getQuestion().equals(questionId))
                .findAny();

        if (question.isPresent()) {
            questionBox.setText(question.get().getQuestion());
            informationBox.setText(question.get().getInformation());
        }
    }

    private void loadOptions(int questionId) {
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.clearCheck();
        radioGroup.removeAllViews();

        List<Option> possibleOptions = options
                .stream()
                .filter(o -> o.getQuestionId() == questionId)
                .collect(Collectors.toList());

        for (Option o : possibleOptions) {
            AppCompatRadioButton optionBox = new AppCompatRadioButton(this);

            optionBox.setId(o.getId());
            optionBox.setText(o.getValue());
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
        Optional<Answer> answer = currentAnswers
                .stream()
                .filter(a -> a.getQuestionId() == questionId)
                .findAny();

        if (answer.isPresent()) {
            AppCompatRadioButton option = findViewById(answer.get().getOptionId());
            option.setChecked(true);
        }
    }

    private void presetWakeUpTime(int questionId) {
        Optional<Answer> previousAnswer = currentAnswers
                .stream()
                .filter(a -> a.getQuestionId() == (questionId - 1))
                .findAny();

        Optional<Option> firstOption = options
                .stream()
                .filter(o -> o.getQuestionId() == questionId)
                .findFirst();

        if (previousAnswer.isPresent() && firstOption.isPresent()) {
            AppCompatRadioButton option = findViewById(previousAnswer.get().getOptionId() + firstOption.get().getId() - 1);

            TextBox information = findViewById(R.id.information);
            String currentText = information.getText().toString();
            String newText = getString(R.string.wakeup_time) + option.getText().toString().toLowerCase();

            information.setText(currentText + "\n " + newText);

            Optional<Answer> currentAnswer = currentAnswers
                    .stream()
                    .filter(a -> a.getQuestionId() == questionId)
                    .findAny();

            if(!currentAnswer.isPresent()) {
                option.setChecked(true);
            }
        }
    }

    private void loadAllAnswers() {
        LinearLayout layout = findViewById(R.id.answers);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, sizeInDp / 2);

        for (Question q : questions) {
            TextBox textBox = new TextBox(this);

            textBox.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            textBox.setTextSize((int) (sizeInDp / 3.5));
            textBox.setLayoutParams(layoutParams);

            int questionId = q.getId();

            String questionText = questionId + ". " + q.getQuestion();
            String answerText;

            Optional<Answer> currentAnswer = currentAnswers
                    .stream()
                    .filter(a -> a.getQuestionId() == questionId)
                    .findAny();

            if (currentAnswer.isPresent()) {
                Optional<Option> option = options
                        .stream()
                        .filter(o -> o.getId() == currentAnswer.get().getOptionId())
                        .findAny();

                answerText = "A: " + option.get().getValue();
            }
            else {
                answerText = "No answer";
            }

            textBox.setText(questionText + "\n" + answerText);

            layout.addView(textBox);
        }
    }

    public void storeAnswers(View view) {
        db.answerDao().insert(currentAnswers);

        Intent homeScreen = new Intent(this, HomeScreen.class);
        startActivity(homeScreen);
    }
}
