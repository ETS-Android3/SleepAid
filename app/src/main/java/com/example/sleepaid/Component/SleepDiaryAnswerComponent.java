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

import com.example.sleepaid.R;
import com.google.android.material.textfield.TextInputLayout;

@SuppressLint("ResourceType")
public class SleepDiaryAnswerComponent extends FrameLayout {
    TextInputLayout answerContainer;
    AutoCompleteTextView answerText;

    public SleepDiaryAnswerComponent(Context context) {
        super(context);
        init(context);
    }

    public SleepDiaryAnswerComponent(Context context, AttributeSet attributes) {
        super(context, attributes);
        init(context, attributes);
    }

    private void init(Context context) {
        inflate(context, R.layout.sleep_diary_answer_component, this);

        int[] sets = {R.attr.hint, R.attr.inputType, R.attr.maxLength};

        TypedArray typedArray = context.obtainStyledAttributes(sets);
        CharSequence hint = typedArray.getText(0);
        CharSequence inputType = typedArray.getText(1);
        int maxLength = typedArray.getInt(2, 5);

        typedArray.recycle();

        initComponents();

        setHint(hint);
        setInputType(inputType);
        setMaxLength(maxLength);
    }

    private void init(Context context, AttributeSet attributes) {
        inflate(context, R.layout.sleep_diary_answer_component, this);

        int[] sets = {R.attr.hint, R.attr.inputType, R.attr.maxLength};

        TypedArray typedArray = context.obtainStyledAttributes(attributes, sets);
        CharSequence hint = typedArray.getText(0);
        CharSequence inputType = typedArray.getText(1);
        int maxLength = typedArray.getInt(2, 5);

        typedArray.recycle();

        initComponents();

        setHint(hint);
        setInputType(inputType);
        setMaxLength(maxLength);
    }

    private void initComponents() {
        answerContainer = findViewById(R.id.answerContainer);
        answerText = findViewById(R.id.answerText);
    }

    public void setText(CharSequence text) {
        answerText.setText(text);
    }

    public void setHint(CharSequence hint) {
        answerText.setHint(hint);
    }

    public void setError(CharSequence error) {
        answerContainer.setError(error);

        if (error == null) {
            answerContainer.setErrorEnabled(false);
        }
    }

    public void setInputType(CharSequence inputType) {
        switch (inputType.toString()) {
            case "textShortMessage":
                answerText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
                break;

            case "number":
                answerText.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;

            default:
                answerText.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME);
                break;
        }
    }

    public void setMaxLength(int maxLength) {
        answerText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(maxLength) });
    }

    public void setAdapter(ArrayAdapter<String> arrayAdapter) {
        answerText.setAdapter(arrayAdapter);
    }

    public void setOnTouchListener(OnTouchListener listener) {
        answerText.setOnTouchListener(listener);
    }

    public void setSelection(int index) {
        answerText.setSelection(index);
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        answerText.addTextChangedListener(textWatcher);
    }

    public void showDropDown() {
        answerText.showDropDown();
    }

    public CharSequence getText() {
        return answerText.getText();
    }

    public int getInputType() {
        return answerText.getInputType();
    }

    public int length() {
        return answerText.length();
    }
}
