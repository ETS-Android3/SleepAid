package com.example.sleepaid.Component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.sleepaid.R;

@SuppressLint("ResourceType")
public class ErrorMessage extends FrameLayout {
    TextView error;

    public ErrorMessage(Context context) {
        super(context);
        init(context);
    }

    public ErrorMessage(Context context, AttributeSet attributes) {
        super(context, attributes);
        init(context, attributes);
    }

    private void init(Context context) {
        inflate(context, R.layout.error_message, this);

        int[] sets = {R.attr.errorText, R.attr.errorTextSize};

        CharSequence errorText = null;
        float errorTextSize = 0;

        TypedArray typedArray = context.obtainStyledAttributes(sets);

        if (typedArray.hasValue(0)) {
            errorText = typedArray.getText(0);
        }


        if (typedArray.hasValue(1)) {
            errorTextSize = typedArray.getFloat(1, 20);
        }

        typedArray.recycle();

        initComponents();

        if (errorText != null) {
            setText(errorText);
        }

        if (errorTextSize != 0) {
            setTextSize(errorTextSize);
        }
    }

    private void init(Context context, AttributeSet attributes) {
        inflate(context, R.layout.error_message, this);

        int[] sets = {R.attr.errorText, R.attr.errorTextSize};

        CharSequence errorText = null;
        float errorTextSize = 0;

        TypedArray typedArray = context.obtainStyledAttributes(attributes, sets);

        if (typedArray.hasValue(0)) {
            errorText = typedArray.getText(0);
        }


        if (typedArray.hasValue(1)) {
            errorTextSize = typedArray.getFloat(1, 20);
        }

        typedArray.recycle();

        initComponents();

        if (errorText != null) {
            setText(errorText);
        }

        if (errorTextSize != 0) {
            setTextSize(errorTextSize);
        }
    }

    private void initComponents() {
        error = findViewById(R.id.error);
    }

    public void setText(CharSequence text) {
        error.setText(text);
    }

    public void setTextSize(float textSize) {
        error.setTextSize(textSize);
    }
}
