package com.example.sleepaid.MainMenu.Fragment.Goals;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.sleepaid.AlarmAdapter;
import com.example.sleepaid.App;
import com.example.sleepaid.DataHandler;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Database.Goal.Goal;
import com.example.sleepaid.Database.SleepData.SleepData;
import com.example.sleepaid.GoalAdapter;
import com.example.sleepaid.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@SuppressLint("NewApi")
public class GoalsFragment extends Fragment {
    AppDatabase db;

    List<Goal> goalList;
    List<SleepData> sleepDataList;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_goals, container, false);
    }

    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        db = AppDatabase.getDatabase(App.getContext());

        loadGoals();
    }

    private void loadGoals() {
        db.goalDao()
                .getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        goalData -> {
                            this.goalList = goalData;
                            loadPercentages();
                        },
                        Throwable::printStackTrace
                );
    }

    private void loadPercentages() {
        db.sleepDataDao()
                .getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sleepData -> {
                            this.sleepDataList = sleepData;
                            loadGoalList();
                        },
                        Throwable::printStackTrace
                );
    }

    protected void loadGoalList() {
        ListView list = getView().findViewById(R.id.goalsList);

        if (goalList.size() != 0) {
            list.setVisibility(View.VISIBLE);

            List<String> goalTexts = new ArrayList<>();
            List<String> percentages = new ArrayList<>();

            for (Goal g : goalList) {
                if (g.getValueMin() != g.getValueMax()) {
                    goalTexts.add(g.getValueMin() + " - " + g.getValueMax());
                } else {
                    if (g.getName().contains("duration")) {
                        goalTexts.add(g.getValueMin() + "h");
                    } else {
                        goalTexts.add(Integer.toString(g.getValueMin()));
                    }
                }

                List<SleepData> goalData = sleepDataList.stream().filter(s -> s.getField().equals(g.getName())).collect(Collectors.toList());
                double percent = 0;

                for (SleepData s : goalData) {
                    if (DataHandler.getDoubleFromSleepDataValue(s) <= g.getValueMax() &&
                            DataHandler.getDoubleFromSleepDataValue(s) >= g.getValueMin()) {
                        percent++;
                    }
                }

                percent = goalData.size() == 0 ? 0 : percent / goalData.size() * 100;

                percentages.add(String.format("%.2f", percent));
            }

            GoalAdapter goalAdapter = new GoalAdapter(
                    App.getContext(),
                    goalList.stream().map(Goal::getName).collect(Collectors.toList()),
                    goalTexts,
                    percentages
            );

            list.setAdapter(goalAdapter);
        } else {
            list.setVisibility(View.INVISIBLE);
        }
    }
}