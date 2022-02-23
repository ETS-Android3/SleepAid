package com.example.sleepaid.MainMenu.Fragment.SleepDiary;

import android.os.Bundle;
import android.text.InputType;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sleepaid.App;
import com.example.sleepaid.R;
import com.example.sleepaid.TextBox;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MorningSleepDiaryFragment extends Fragment {
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
        q1.setText("Today, around what time did you have breakfast? (if none, write “none”)");
        q1.setTextColor(getResources().getColor(R.color.white));
        q1.setTextSize(20);
        q1.setPadding(0, 0, 0, 15);

        EditText a1 = new EditText(App.getContext());
        a1.setInputType(InputType.TYPE_CLASS_DATETIME |InputType.TYPE_DATETIME_VARIATION_TIME);
        a1.setBackgroundColor(getResources().getColor(R.color.white));

        layout.addView(q1);
        layout.addView(a1);

        TextView q2 = new TextView(App.getContext());
        q2.setText("Today, around what time did you have lunch? (if none, write “none”)");
        q2.setTextColor(getResources().getColor(R.color.white));
        q2.setTextSize(20);
        q2.setPadding(0, 0, 0, 15);

        EditText a2 = new EditText(App.getContext());
        a2.setInputType(InputType.TYPE_CLASS_DATETIME |InputType.TYPE_DATETIME_VARIATION_TIME);
        a2.setBackgroundColor(getResources().getColor(R.color.white));

        layout.addView(q2);
        layout.addView(a2);

        TextView q3 = new TextView(App.getContext());
        q3.setText("Today, around what time did you have dinner? (if none, write “none”)");
        q3.setTextColor(getResources().getColor(R.color.white));
        q3.setTextSize(20);
        q3.setPadding(0, 0, 0, 15);

        EditText a3 = new EditText(App.getContext());
        a3.setInputType(InputType.TYPE_CLASS_DATETIME |InputType.TYPE_DATETIME_VARIATION_TIME);
        a3.setBackgroundColor(getResources().getColor(R.color.white));

        layout.addView(q3);
        layout.addView(a3);

        TextView q4 = new TextView(App.getContext());
        q4.setText("What exercise did you do today? Please give approximate times for each.");
        q4.setTextColor(getResources().getColor(R.color.white));
        q4.setTextSize(20);
        q4.setPadding(0, 0, 0, 15);

        EditText a4 = new EditText(App.getContext());
        a4.setInputType(InputType.TYPE_CLASS_TEXT);
        a4.setBackgroundColor(getResources().getColor(R.color.white));

        layout.addView(q4);
        layout.addView(a4);

        TextView q5 = new TextView(App.getContext());
        q5.setText("How many daytime naps did you take today? Please give approximate times for each.");
        q5.setTextColor(getResources().getColor(R.color.white));
        q5.setTextSize(20);
        q5.setPadding(0, 0, 0, 15);

        EditText a5 = new EditText(App.getContext());
        a5.setInputType(InputType.TYPE_CLASS_NUMBER);
        a5.setBackgroundColor(getResources().getColor(R.color.white));

        layout.addView(q5);
        layout.addView(a5);

        TextView q6 = new TextView(App.getContext());
        q6.setText("How much time did you roughly spend using technology (for example: using your phone, watching TV, being on a computer) in the past 2 hours?");
        q6.setTextColor(getResources().getColor(R.color.white));
        q6.setTextSize(20);
        q6.setPadding(0, 0, 0, 15);

        EditText a6 = new EditText(App.getContext());
        a6.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME);
        a6.setBackgroundColor(getResources().getColor(R.color.white));

        layout.addView(q6);
        layout.addView(a6);
    }
}