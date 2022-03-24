package com.example.sleepaid.Fragment.SleepDiary;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.sleepaid.App;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Database.Option.Option;
import com.example.sleepaid.Model.SharedViewModel;
import com.example.sleepaid.R;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

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

        this.optionComponentIds = new int[][]{
                {R.id.bedtimeAnswer1},
                {R.id.bedtimeAnswer2},
                {R.id.bedtimeAnswer3},
                {R.id.bedtimeAnswer4Text, R.id.bedtimeAnswer4EndTime, R.id.bedtimeAnswer4StartTime},
                {R.id.bedtimeAnswer5Text},
                {R.id.bedtimeAnswer6}
        };

        super.onViewCreated(view, savedInstanceState);
    }
}