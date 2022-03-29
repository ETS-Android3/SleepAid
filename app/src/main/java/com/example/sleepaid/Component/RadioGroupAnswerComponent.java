package com.example.sleepaid.Component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioGroup;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.sleepaid.R;

@SuppressLint("ResourceType")
public class RadioGroupAnswerComponent extends ConstraintLayout {
    TextBox subQuestion;
    RadioGroup radioGroup;
    ErrorMessage errorMessage;

    public RadioGroupAnswerComponent(Context context) {
        super(context);
        init(context);
    }

    public RadioGroupAnswerComponent(Context context, AttributeSet attributes) {
        super(context, attributes);
        init(context, attributes);
    }

    private void init(Context context) {
        inflate(context, R.layout.radio_group_answer_component, this);

        initComponents();
    }
    private void init(Context context, AttributeSet attributes) {
        inflate(context, R.layout.radio_group_answer_component, this);

        initComponents();
    }

    private void initComponents() {
        subQuestion = findViewById(R.id.subQuestion);
        radioGroup = findViewById(R.id.radioGroup);
        errorMessage = findViewById(R.id.errorMessage);
    }

    public void setSubQuestion(CharSequence question) {
        subQuestion.setText(question);
    }

    public void setError(CharSequence error) {
        if (error == null) {
            errorMessage.setVisibility(GONE);
        } else {
            errorMessage.setVisibility(VISIBLE);
            errorMessage.setText(error);
        }
    }

    public void setEnabled(boolean enabled) {
        for (int r = 0; r < radioGroup.getChildCount(); r++) {
            radioGroup.getChildAt(r).setEnabled(enabled);
        }
    }

    public void setOnClickListener(OnClickListener listener) {
        for (int r = 0; r < radioGroup.getChildCount(); r++) {
            radioGroup.getChildAt(r).setOnClickListener(listener);
        }
    }

    public RadioGroup getRadioGroup() {
        return radioGroup;
    }

    public int getCheckedRadioButtonId() {
        return radioGroup.getCheckedRadioButtonId();
    }

    public void clearCheck() {
        radioGroup.clearCheck();
    }
}
