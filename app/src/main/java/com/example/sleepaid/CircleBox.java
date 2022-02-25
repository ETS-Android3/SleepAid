package com.example.sleepaid;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

public class CircleBox extends FrameLayout {
    TextView text;

    public CircleBox(Context context) {
        super(context);
        init(context);
    }

    public CircleBox(Context context, AttributeSet attributes) {
        super(context, attributes);
        init(context, attributes);
    }

    private void init(Context context) {
        inflate(context, R.layout.circle_box, this);

        int[] sets = {R.attr.text};
        TypedArray typedArray = context.obtainStyledAttributes(sets);
        CharSequence text = typedArray.getText(0);
        typedArray.recycle();

        initComponents();
        setText(text);
    }

    private void init(Context context, AttributeSet attributes) {
        inflate(context, R.layout.circle_box, this);

        int[] sets = {R.attr.text};
        TypedArray typedArray = context.obtainStyledAttributes(attributes, sets);
        CharSequence text = typedArray.getText(0);
        typedArray.recycle();

        initComponents();
        setText(text);
    }

    private void initComponents() {
        text = (TextView) findViewById(R.id.box_Text);
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
