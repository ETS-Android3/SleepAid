package com.example.sleepaid.Fragment.Goals;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sleepaid.Activity.MainMenuScreen;
import com.example.sleepaid.Adapter.GoalAdapter;
import com.example.sleepaid.App;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Database.Goal.Goal;
import com.example.sleepaid.Database.Notification.Notification;
import com.example.sleepaid.Database.SleepData.SleepData;
import com.example.sleepaid.Fragment.MainMenuFragment;
import com.example.sleepaid.Handler.DataHandler;
import com.example.sleepaid.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GoalsFragment extends MainMenuFragment {
    AppDatabase db;

    List<Goal> goalList;
    List<SleepData> sleepDataList;

    List<String> options;
    HashMap<String, List<String>> goalOptions = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_goals, container, false);
    }

    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        this.db = AppDatabase.getDatabase(App.getContext());

        this.options = Arrays.asList(
                "View graph breakdown",
                "Edit goal"
        );

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

                            for (Goal g : goalData) {
                                this.goalOptions.put(g.getName(), this.options);
                            }

                            loadPercentages();
                        },
                        Throwable::printStackTrace
                );
    }

    private void loadPercentages() {
        this.db.sleepDataDao()
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
        ExpandableListView list = getView().findViewById(R.id.goalsList);

        if (this.goalList.size() != 0) {
            list.setVisibility(View.VISIBLE);

            List<String> goalTexts = new ArrayList<>();
            List<String> percentages = new ArrayList<>();

            for (Goal g : goalList) {
                if (!g.getValueMin().equals(g.getValueMax())) {
                    goalTexts.add(g.getValueMin() + " - " + g.getValueMax());
                } else {
                    goalTexts.add(g.getValueMin());
                }

                List<SleepData> goalData = sleepDataList
                        .stream()
                        .filter(s -> s.getField().equals(g.getName()))
                        .collect(Collectors.toList());

                float percent = 0;

                for (SleepData s : goalData) {
                    float goalMax;
                    float goalMin;

                    if (g.getName().equals("Bedtime")) {
                        goalMax = DataHandler.getFloatFromTime(g.getValueMax()) > 12 ?
                                DataHandler.getFloatFromTime(g.getValueMax()) :
                                DataHandler.getFloatFromTime(g.getValueMax()) + 24;

                        goalMin = DataHandler.getFloatFromTime(g.getValueMin()) > 12 ?
                                DataHandler.getFloatFromTime(g.getValueMin()) :
                                DataHandler.getFloatFromTime(g.getValueMin()) + 24;
                    } else {
                        goalMax = DataHandler.getFloatFromTime(g.getValueMax());
                        goalMin = DataHandler.getFloatFromTime(g.getValueMin());
                    }

                    if (DataHandler.getFloatFromTime(s.getValue()) <= goalMax &&
                            DataHandler.getFloatFromTime(s.getValue()) >= goalMin) {
                        percent++;
                    }
                }

                percent = goalData.size() == 0 ? 0 : percent / goalData.size() * 100;

                percentages.add(String.format("%.2f", percent));
                if (percent > 75) {
                    Notification progressNotification = new Notification(
                            "You're doing great!",
                            "You\'ve achieved " + String.format("%.2f", percent) + "% of your " + g.getName().toLowerCase() + " goal.\nKeep doing what you're doing!",
                            DataHandler.getFormattedTime(16, 0),
                            0,
                            0
                    );
                    progressNotification.setId((int) System.currentTimeMillis());
                    progressNotification.schedule(requireActivity());
                }
            }

            List<String> names = this.goalList.stream().map(Goal::getName).collect(Collectors.toList());

            GoalAdapter goalAdapter = new GoalAdapter(
                    App.getContext(),
                    names,
                    goalTexts,
                    percentages,
                    this.goalOptions
            );

            list.setAdapter(goalAdapter);

            list.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
                String option = this.goalOptions.get(names.get(groupPosition)).get(childPosition);
                if (option.equals("View graph breakdown")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("DESTINATION", names.get(groupPosition));

                    Intent intent = new Intent(App.getContext(), MainMenuScreen.class);
                    intent.putExtra("PARENT_DESTINATION", R.id.sleepDataFragment);

                    switch (names.get(groupPosition)) {
                        case "Wake-up time":
                            intent.putExtra("DESTINATION", R.id.wakeupTimeGraphFragment);
                            break;

                        case "Bedtime":
                            intent.putExtra("DESTINATION", R.id.bedtimeGraphFragment);
                            break;

                        case "Technology use":
                            intent.putExtra("DESTINATION", R.id.technologyUseGraphFragment);
                            break;

                        default:
                            intent.putExtra("DESTINATION", R.id.sleepDurationGraphFragment);
                            break;
                    }

                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent);
                } else if (option.equals("Edit goal")) {
                    Intent intent = new Intent(App.getContext(), MainMenuScreen.class);
                    intent.putExtra("PARENT_DESTINATION", R.id.settingsFragment);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent);
                }

                return false;
            });
        } else {
            list.setVisibility(View.INVISIBLE);
        }
    }
}