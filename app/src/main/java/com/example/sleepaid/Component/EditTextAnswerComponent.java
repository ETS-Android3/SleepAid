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

import com.example.sleepaid.R;
import com.google.android.material.textfield.TextInputLayout;

@SuppressLint("ResourceType")
public class EditTextAnswerComponent extends FrameLayout {
    TextInputLayout answerContainer;
    AutoCompleteTextView answerText;
    ErrorMessage errorMessage;

    public EditTextAnswerComponent(Context context) {
        super(context);
        init(context);
    }

    public EditTextAnswerComponent(Context context, AttributeSet attributes) {
        super(context, attributes);
        init(context, attributes);
    }

    private void init(Context context) {
        inflate(context, R.layout.edit_text_answer_component, this);

        int[] sets = {R.attr.hint, R.attr.inputType, R.attr.maxLength, R.attr.editTextTextSize};

        TypedArray typedArray = context.obtainStyledAttributes(sets);

        CharSequence hint = null;
        if (typedArray.hasValue(0)) {
            hint = typedArray.getText(0);
        }

        CharSequence inputType = null;
        if (typedArray.hasValue(1)) {
            inputType = typedArray.getText(1);
        }

        int maxLength = 0;
        if (typedArray.hasValue(2)) {
            maxLength = typedArray.getInt(2, 5);
        }

        float textSize = 0;
        if (typedArray.hasValue(3)) {
            textSize = typedArray.getFloat(3, 15);
        }

        typedArray.recycle();

        initComponents();

        if (hint != null) {
            setHint(hint);
        }

        if (inputType != null) {
            setInputType(inputType);
        }

        if (maxLength != 0) {
            setMaxLength(maxLength);
        }

        if (textSize != 0) {
            setTextSize(textSize);
        }
    }

    private void init(Context context, AttributeSet attributes) {
        inflate(context, R.layout.edit_text_answer_component, this);

        int[] sets = {R.attr.hint, R.attr.inputType, R.attr.maxLength, R.attr.editTextTextSize};

        TypedArray typedArray = context.obtainStyledAttributes(attributes, sets);

        CharSequence hint = null;
        if (typedArray.hasValue(0)) {
            hint = typedArray.getText(0);
        }

        CharSequence inputType = null;
        if (typedArray.hasValue(1)) {
            inputType = typedArray.getText(1);
        }

        int maxLength = 0;
        if (typedArray.hasValue(2)) {
            maxLength = typedArray.getInt(2, 5);
        }

        float textSize = 0;
        if (typedArray.hasValue(3)) {
            textSize = typedArray.getFloat(3, 15);
        }

        typedArray.recycle();

        initComponents();

        if (hint != null) {
            setHint(hint);
        }

        if (inputType != null) {
            setInputType(inputType);
        }

        if (maxLength != 0) {
            setMaxLength(maxLength);
        }

        if (textSize != 0) {
            setTextSize(textSize);
        }
    }

    private void initComponents() {
        this.answerContainer = findViewById(R.id.answerContainer);
        this.answerText = findViewById(R.id.answerText);
        this.errorMessage = findViewById(R.id.errorMessage);
    }

    public void setText(CharSequence text) {
        this.answerText.setText(text);
    }

    public void setTextSize(float textSize) {
        this.answerText.setTextSize(textSize);
    }

    public void setHint(CharSequence hint) {
        this.answerText.setHint(hint);
    }

    public void setError(CharSequence error) {
        if (error == null) {
            this.errorMessage.setVisibility(GONE);
        } else {
            this.errorMessage.setVisibility(VISIBLE);
            this.errorMessage.setText(error);
        }
    }

    public void setInputType(CharSequence inputType) {
        switch (inputType.toString()) {
            case "text":
                this.answerText.setInputType(InputType.TYPE_CLASS_TEXT);
                break;

            case "textShortMessage":
                this.answerText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
                break;

            case "number":
                this.answerText.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;

            case "float":
                this.answerText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                break;

            default:
                this.answerText.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME);
                break;
        }
    }

    public void setMaxLength(int maxLength) {
        this.answerText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(maxLength) });
    }

    public void setAdapter(ArrayAdapter<String> arrayAdapter) {
        this.answerText.setAdapter(arrayAdapter);
    }

    public void setOnTouchListener(OnTouchListener listener) {
        this.answerText.setOnTouchListener(listener);
    }

    public void setOnEditorActionListener(TextView.OnEditorActionListener listener) {
        this.answerText.setOnEditorActionListener(listener);
    }

    public void setSelection(int index) {
        this.answerText.setSelection(index);
    }

    public void setEnabled(boolean enabled) {
        this.answerText.setEnabled(enabled);
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        this.answerText.addTextChangedListener(textWatcher);
    }

    public void showDropDown() {
        this.answerText.showDropDown();
    }

    public CharSequence getText() {
        return this.answerText.getText();
    }

    public int getInputType() {
        return this.answerText.getInputType();
    }

    public int length() {
        return this.answerText.length();
    }

    public void clear() {
        this.answerText.getText().clear();
    }
}
