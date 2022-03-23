package com.example.sleepaid.Fragment.SleepDiary;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.sleepaid.App;
import com.example.sleepaid.Database.Answer.Answer;
import com.example.sleepaid.Database.Option.Option;
import com.example.sleepaid.Model.SharedViewModel;
import com.example.sleepaid.R;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

        this.model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        this.questionIds = new int[]{
                R.id.bedtimeQuestion1,
                R.id.bedtimeQuestion2,
                R.id.bedtimeQuestion3,
                R.id.bedtimeQuestion4,
                R.id.bedtimeQuestion5,
                R.id.bedtimeQuestion6
        };

        this.optionIds = new int[][]{
                {R.id.bedtimeAnswer1},
                {R.id.bedtimeAnswer2},
                {R.id.bedtimeAnswer3},
                {R.id.bedtimeAnswer4Text, R.id.bedtimeAnswer4EndTime, R.id.bedtimeAnswer4StartTime},
                {R.id.bedtimeAnswer5Text},
                {R.id.bedtimeAnswer6}
        };

        if (model.getOptions(this.questionnaireId) != null) {
            Context context = App.getContext();
            int layout = android.R.layout.simple_dropdown_item_1line;

            List<Option> suggestions = model.getOptions(this.questionnaireId)
                    .stream()
                    .collect(Collectors.toList());

            List<Integer> questionIds = model.getOptions(this.questionnaireId)
                    .stream()
                    .map(o -> o .getQuestionId())
                    .collect(Collectors.toList());

            Collections.sort(suggestions);
            Collections.sort(questionIds);

            this.optionSuggestions = new ArrayAdapter[6][];

            for (int i = 0; i < questionIds.size(); i++) {
                int finalI = i;

                List<String> suggestionsForQuestion = suggestions.stream()
                        .filter(s -> s.getQuestionId() == finalI)
                        .map(s -> s.getValue())
                        .collect(Collectors.toList());

                Collections.sort(suggestionsForQuestion);

                this.optionSuggestions[i] = new ArrayAdapter[] {
                        new ArrayAdapter(context, layout, suggestionsForQuestion)
                };
            }
        }

        super.onViewCreated(view, savedInstanceState);
    }
}