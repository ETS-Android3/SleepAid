package com.example.sleepaid.Fragment.SleepDiary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sleepaid.R;

public class BedtimeSleepDiaryFragment extends SleepDiaryQuestionsFragment {
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sleep_diary_bedtime_questions, container, false);
    }

    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        this.questionnaireId = 5;

        this.questionComponentIds = new int[]{
                R.id.bedtimeQuestion1,
                R.id.bedtimeQuestion2,
                R.id.bedtimeQuestion3,
                R.id.bedtimeQuestion4,
                R.id.bedtimeQuestion5,
                R.id.bedtimeQuestion6
        };

        this.answerComponentIds = new int[][]{
                {R.id.bedtimeAnswer1},
                {R.id.bedtimeAnswer2},
                {R.id.bedtimeAnswer3},
                {R.id.bedtimeAnswer4Text, R.id.bedtimeAnswer4StartTime, R.id.bedtimeAnswer4EndTime},
                {R.id.bedtimeAnswer5Text},
                {R.id.bedtimeAnswer6}
        };

        this.sections = new int[][] {
                {1},
                {1},
                {1},
                {1, 2, 3},
                {1},
                {1}
        };

        this.answerSuggestions = new ArrayAdapter[][]{
                {null},
                {null},
                {null},
                {null, null, null},
                {null, null, null},
                {null}
        };

        this.emptyErrors = new String[][]{
                {"Please enter a time or \"0\" if you had none."},
                {"Please enter a time or \"0\" if you had none."},
                {"Please enter a time or \"0\" if you had none."},
                {"Please enter an answer or \"none\" if you did none.", "Please enter a time.", "Please enter a time."},
                {"Please enter a number.", "Please enter a time.", "Please enter a time."},
                {"Please enter a duration."}
        };

        super.onViewCreated(view, savedInstanceState);
    }
}