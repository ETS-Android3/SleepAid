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
import android.widget.RadioGroup;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.sleepaid.R;
import com.google.android.material.textfield.TextInputLayout;

@SuppressLint("ResourceType")
public class SleepDiaryRadioGroupAnswerComponent extends ConstraintLayout {
    RadioGroup radioGroup;
    ErrorMessage errorMessage;

    public SleepDiaryRadioGroupAnswerComponent(Context context) {
        super(context);
        init(context);
    }

    public SleepDiaryRadioGroupAnswerComponent(Context context, AttributeSet attributes) {
        super(context, attributes);
        init(context, attributes);
    }

    private void init(Context context) {
        inflate(context, R.layout.sleep_diary_radio_group_answer_component, this);

        initComponents();
    }
    private void init(Context context, AttributeSet attributes) {
        inflate(context, R.layout.sleep_diary_radio_group_answer_component, this);

        initComponents();
    }

    private void initComponents() {
        radioGroup = findViewById(R.id.radioGroup);
        errorMessage = findViewById(R.id.errorMessage);
    }

    public void setError(CharSequence error) {
        if (error == null) {
            errorMessage.setVisibility(GONE);
        } else {
            errorMessage.setVisibility(VISIBLE);
            errorMessage.setText(error);
        }
    }

    public RadioGroup getRadioGroup() {
        return radioGroup;
    }
}
