package com.example.sleepaid;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

public class TextBox extends FrameLayout {
    TextView text;

    public TextBox(Context context, AttributeSet attributes) {
        super(context, attributes);
        init(context, attributes);
    }

    private void init(Context context, AttributeSet attributes) {
        inflate(context, R.layout.text_box, this);

        int[] sets = {R.attr.text};
        TypedArray typedArray = context.obtainStyledAttributes(attributes, sets);
        CharSequence boxText = typedArray.getText(0);
        typedArray.recycle();

        initComponents();
        setText(boxText);
    }

    private void initComponents() {
        text = (TextView) findViewById(R.id.box_Text);
    }

    public void setText(CharSequence value) {
        text.setText(value);
    }
}
