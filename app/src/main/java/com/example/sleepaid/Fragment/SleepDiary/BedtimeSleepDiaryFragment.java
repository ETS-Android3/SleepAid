package com.example.sleepaid.Fragment.SleepDiary;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sleepaid.App;
import com.example.sleepaid.R;

public class BedtimeSleepDiaryFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sleep_diary_questions, container, false);
    }

    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        getParentFragment().getParentFragment().getView().findViewById(R.id.scrollView).scrollTo(0, 0);

        LinearLayout layout = getView().findViewById(R.id.questions);
        layout.removeAllViews();

        TextView q1 = new TextView(App.getContext());
        q1.setText("Last night, around what time did you go to bed?");
        q1.setTextColor(getResources().getColor(R.color.white));
        q1.setTextSize(20);
        q1.setPadding(0, 0, 0, 15);

        EditText a1 = new EditText(App.getContext());
        a1.setInputType(InputType.TYPE_CLASS_DATETIME |InputType.TYPE_DATETIME_VARIATION_TIME);
        a1.setBackgroundColor(getResources().getColor(R.color.white));

        layout.addView(q1);
        layout.addView(a1);

        TextView q2 = new TextView(App.getContext());
        q2.setText("Last night, around what time did you turn your lights out?");
        q2.setTextColor(getResources().getColor(R.color.white));
        q2.setTextSize(20);
        q2.setPadding(0, 0, 0, 15);

        EditText a2 = new EditText(App.getContext());
        a2.setInputType(InputType.TYPE_CLASS_DATETIME |InputType.TYPE_DATETIME_VARIATION_TIME);
        a2.setBackgroundColor(getResources().getColor(R.color.white));

        layout.addView(q2);
        layout.addView(a2);

        TextView q3 = new TextView(App.getContext());
        q3.setText("Last night, roughly how long did it take you to fall asleep?");
        q3.setTextColor(getResources().getColor(R.color.white));
        q3.setTextSize(20);
        q3.setPadding(0, 0, 0, 15);

        EditText a3 = new EditText(App.getContext());
        a3.setInputType(InputType.TYPE_CLASS_DATETIME |InputType.TYPE_DATETIME_VARIATION_TIME);
        a3.setBackgroundColor(getResources().getColor(R.color.white));

        layout.addView(q3);
        layout.addView(a3);

        TextView q4 = new TextView(App.getContext());
        q4.setText("This morning, roughly how long did it take you to wake up?");
        q4.setTextColor(getResources().getColor(R.color.white));
        q4.setTextSize(20);
        q4.setPadding(0, 0, 0, 15);

        EditText a4 = new EditText(App.getContext());
        a4.setInputType(InputType.TYPE_CLASS_TEXT);
        a4.setBackgroundColor(getResources().getColor(R.color.white));

        layout.addView(q4);
        layout.addView(a4);

        TextView q5 = new TextView(App.getContext());
        q5.setText("This morning, what were you awakened by?");
        q5.setTextColor(getResources().getColor(R.color.white));
        q5.setTextSize(20);
        q5.setPadding(0, 0, 0, 15);

        EditText a5 = new EditText(App.getContext());
        a5.setInputType(InputType.TYPE_CLASS_NUMBER);
        a5.setBackgroundColor(getResources().getColor(R.color.white));

        layout.addView(q5);
        layout.addView(a5);

        TextView q6 = new TextView(App.getContext());
        q6.setText("After you fell asleep last night, roughly how many times did you wake up during the night?");
        q6.setTextColor(getResources().getColor(R.color.white));
        q6.setTextSize(20);
        q6.setPadding(0, 0, 0, 15);

        EditText a6 = new EditText(App.getContext());
        a6.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME);
        a6.setBackgroundColor(getResources().getColor(R.color.white));

        layout.addView(q6);
        layout.addView(a6);

        TextView q7 = new TextView(App.getContext());
        q7.setText("How would you rate your sleep quality last night?");
        q7.setTextColor(getResources().getColor(R.color.white));
        q7.setTextSize(20);
        q7.setPadding(0, 0, 0, 15);

        EditText a7 = new EditText(App.getContext());
        a7.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME);
        a7.setBackgroundColor(getResources().getColor(R.color.white));

        layout.addView(q7);
        layout.addView(a7);

        TextView q8 = new TextView(App.getContext());
        q8.setText("How would you rate your mood on final wakening this morning?");
        q8.setTextColor(getResources().getColor(R.color.white));
        q8.setTextSize(20);
        q8.setPadding(0, 0, 0, 15);

        EditText a8 = new EditText(App.getContext());
        a8.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME);
        a8.setBackgroundColor(getResources().getColor(R.color.white));

        layout.addView(q8);
        layout.addView(a8);

        TextView q9 = new TextView(App.getContext());
        q9.setText("How would you rate your alertness on final wakening this morning?");
        q9.setTextColor(getResources().getColor(R.color.white));
        q9.setTextSize(20);
        q9.setPadding(0, 0, 0, 15);

        EditText a9 = new EditText(App.getContext());
        a9.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME);
        a9.setBackgroundColor(getResources().getColor(R.color.white));

        layout.addView(q9);
        layout.addView(a9);
    }
}