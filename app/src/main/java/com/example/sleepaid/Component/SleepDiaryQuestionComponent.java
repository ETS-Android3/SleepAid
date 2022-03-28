package com.example.sleepaid.Component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.sleepaid.R;
import com.google.android.material.textfield.TextInputLayout;

@SuppressLint("ResourceType")
public class SleepDiaryQuestionComponent extends ConstraintLayout {
    TextView question;
    TextView information;

    public SleepDiaryQuestionComponent(Context context) {
        super(context);
        init(context);
    }

    public SleepDiaryQuestionComponent(Context context, AttributeSet attributes) {
        super(context, attributes);
        init(context, attributes);
    }

    private void init(Context context) {
        inflate(context, R.layout.sleep_diary_question_component, this);

        int[] sets = {R.attr.questionText, R.attr.informationText};

        TypedArray typedArray = context.obtainStyledAttributes(sets);
        CharSequence questionText = typedArray.getText(0);
        CharSequence informationText = typedArray.getText(1);

        typedArray.recycle();

        initComponents();

        setQuestionText(questionText);
        setInformationText(informationText);
    }

    private void init(Context context, AttributeSet attributes) {
        inflate(context, R.layout.sleep_diary_question_component, this);

        int[] sets = {R.attr.questionText, R.attr.informationText};

        TypedArray typedArray = context.obtainStyledAttributes(attributes, sets);
        CharSequence questionText = typedArray.getText(0);
        CharSequence informationText = typedArray.getText(1);

        typedArray.recycle();

        initComponents();

        setQuestionText(questionText);
        setInformationText(informationText);
    }

    private void initComponents() {
        question = findViewById(R.id.question);
        information = findViewById(R.id.information);
    }

    public void setQuestionText(CharSequence questionText) {
        question.setText(questionText);
    }

    public void setInformationText(CharSequence informationText) {
        information.setText(informationText);
    }

    public void setInformationVisibility(int visibility) {
        information.setVisibility(visibility);
    }
}
