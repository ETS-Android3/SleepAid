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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.sleepaid.App;
import com.example.sleepaid.R;

public class MorningSleepDiaryFragment extends SleepDiaryQuestionsFragment {
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sleep_diary_morning_questions, container, false);
    }

    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.questionIds = new int[]{
                R.id.question1,
                R.id.question2,
                R.id.question3,
                R.id.question4,
                R.id.question5,
                R.id.question6,
                R.id.question7,
                R.id.question8,
                R.id.question9
        };

        this.optionIds = new int[][]{
                {R.id.answer1},
                {R.id.answer2},
                {R.id.answer3},
                {R.id.answer4},
                {R.id.answer5},
                {R.id.answer6},
                {R.id.answer7},
                {R.id.answer8},
                {R.id.answer9}
        };

        super.loadQuestionnaire(4);
    }
}