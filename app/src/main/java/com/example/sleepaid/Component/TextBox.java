package com.example.sleepaid.Component;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.sleepaid.R;

public class TextBox extends FrameLayout {
    TextView text;

    public TextBox(Context context) {
        super(context);
        init(context);
    }

    public TextBox(Context context, AttributeSet attributes) {
        super(context, attributes);
        init(context, attributes);
    }

    private void init(Context context) {
        inflate(context, R.layout.text_box, this);

        int[] sets = {R.attr.text};
        TypedArray typedArray = context.obtainStyledAttributes(sets);
        CharSequence text = typedArray.getText(0);
        typedArray.recycle();

        initComponents();
        setText(text);
    }

    private void init(Context context, AttributeSet attributes) {
        inflate(context, R.layout.text_box, this);

        int[] sets = {R.attr.text};
        TypedArray typedArray = context.obtainStyledAttributes(attributes, sets);
        CharSequence text = typedArray.getText(0);
        typedArray.recycle();

        initComponents();
        setText(text);
    }

    private void initComponents() {
        text = findViewById(R.id.box_Text);
    }

    public void setText(CharSequence value) {
        text.setText(value);
    }

    public CharSequence getText() {
        return text.getText();
    }

    public void setTextAlignment(int value) {
        text.setTextAlignment(value);
    }

    public void setTextSize(int value) {
        text.setTextSize(value);
    }
}
