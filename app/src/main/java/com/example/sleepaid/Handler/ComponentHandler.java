package com.example.sleepaid.Handler;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.util.TypedValue;
import android.view.View;
import android.widget.RadioGroup;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatRadioButton;

import com.example.sleepaid.App;
import com.example.sleepaid.R;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComponentHandler {
    public static void setupRadioGroup(RadioGroup radioGroup,
                                int style,
                                int sizeInDp,
                                List<Integer> ids,
                                List<String> texts,
                                String currentOption,
                                View.OnClickListener onClickListener) {
        Context contextThemeWrapper = new ContextThemeWrapper(App.getContext(), style);

        radioGroup.clearCheck();
        radioGroup.removeAllViews();

        for (int i = 0; i < ids.size(); i++) {
            AppCompatRadioButton optionBox = new AppCompatRadioButton(contextThemeWrapper, null, style);

            optionBox.setId(ids.get(i));
            optionBox.setText(texts.get(i));
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

            if (onClickListener != null) {
                optionBox.setOnClickListener(onClickListener);
            }

            if (currentOption != null && texts.get(i).equals(currentOption)) {
                optionBox.setChecked(true);
            }

            radioGroup.addView(optionBox);
        }
    }
}
