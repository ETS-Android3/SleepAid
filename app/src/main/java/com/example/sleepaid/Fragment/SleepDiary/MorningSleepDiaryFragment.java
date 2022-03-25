package com.example.sleepaid.Fragment.SleepDiary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
        this.questionnaireId = 4;

        this.questionComponentIds = new int[]{
                R.id.morningQuestion1,
                R.id.morningQuestion2,
                R.id.morningQuestion3,
                R.id.morningQuestion4,
                R.id.morningQuestion5,
                R.id.morningQuestion6,
                R.id.morningQuestion7,
                R.id.morningQuestion8,
                R.id.morningQuestion9
        };

        this.optionComponentIds = new int[][]{
                {R.id.morningAnswer1},
                {R.id.morningAnswer2},
                {R.id.morningAnswer3},
                {R.id.morningAnswer4},
                {R.id.morningAnswer5},
                {R.id.morningAnswer6},
                {R.id.morningAnswer7},
                {R.id.morningAnswer8},
                {R.id.morningAnswer9}
        };

        super.onViewCreated(view, savedInstanceState);
    }
}