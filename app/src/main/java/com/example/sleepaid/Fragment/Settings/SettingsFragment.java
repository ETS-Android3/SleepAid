package com.example.sleepaid.Fragment.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.sleepaid.Activity.MainMenuScreen;
import com.example.sleepaid.Adapter.GoalAdapter;
import com.example.sleepaid.Adapter.SettingsAdapter;
import com.example.sleepaid.App;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Database.Goal.Goal;
import com.example.sleepaid.Database.SleepData.SleepData;
import com.example.sleepaid.Fragment.MainMenuFragment;
import com.example.sleepaid.Handler.DataHandler;
import com.example.sleepaid.Model.SharedViewModel;
import com.example.sleepaid.R;

import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SettingsFragment extends MainMenuFragment {
    private AppDatabase db;
    private SharedViewModel model;

    private List<String> settingNames;
    private List<String> settingValues;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        this.db = AppDatabase.getDatabase(App.getContext());
        this.model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        loadGoals();
    }

    private void loadGoals() {
        db.goalDao()
                .loadAllByNames(new String[]{"Bedtime", "Wake-up time"})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        goalData -> {
                            this.settingNames = goalData.stream()
                                .map(Goal::getName)
                                .collect(Collectors.toList());

                            this.settingValues = goalData.stream()
                                    .map(g -> g.getValueMin() + " - " + g.getValueMax())
                                    .collect(Collectors.toList());

                            loadConfiguration();
                        },
                        Throwable::printStackTrace
                );
    }

    private void loadConfiguration() {
        this.db.configurationDao()
                .loadAllByNames(new String[]{"supportNaps"})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        configuration -> {
                            this.settingNames.add("Allow naps");
                            this.settingValues.add(configuration.get(0).getValue());

                            this.loadSettingsList();
                        },
                        Throwable::printStackTrace
                );
    }

    protected void loadSettingsList() {
        ListView list = getView().findViewById(R.id.settingsList);

        if (this.settingNames.size() != 0) {
            list.setVisibility(View.VISIBLE);

            SettingsAdapter settingsAdapter = new SettingsAdapter(
                    requireActivity(),
                    this.model,
                    this.settingNames,
                    this.settingValues
            );

            list.setAdapter(settingsAdapter);
        } else {
            list.setVisibility(View.INVISIBLE);
        }
    }
}