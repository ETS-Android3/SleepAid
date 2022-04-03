package com.example.sleepaid.Fragment.RelaxingActivitiesSuggestions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sleepaid.Adapter.RelaxingActivitySuggestionAdapter;
import com.example.sleepaid.App;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Database.RelaxingActivitySuggestion.RelaxingActivitySuggestion;
import com.example.sleepaid.Fragment.MainMenuFragment;
import com.example.sleepaid.R;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RelaxingActivitiesSuggestionsFragment extends MainMenuFragment {
    AppDatabase db;

    List<String> names;
    HashMap<String, List<String>> information = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_relaxing_activities_suggestions, container, false);
    }

    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        this.db = AppDatabase.getDatabase(App.getContext());

        loadGoals();
    }

    private void loadGoals() {
        db.relaxingActivitySuggestionDao()
                .getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        relaxingActivitySuggestionData -> {
                            this.names = relaxingActivitySuggestionData.stream()
                                .map(RelaxingActivitySuggestion::getName)
                                .collect(Collectors.toList());

                            for (RelaxingActivitySuggestion s : relaxingActivitySuggestionData) {
                                this.information.put(s.getName(), Collections.singletonList(s.getInformation()));
                            }

                            loadSuggestionList();
                        },
                        Throwable::printStackTrace
                );
    }

    protected void loadSuggestionList() {
        ExpandableListView list = getView().findViewById(R.id.relaxingActivitiesList);

        if (this.names.size() != 0) {
            list.setVisibility(View.VISIBLE);

            RelaxingActivitySuggestionAdapter relaxingActivityAdapter = new RelaxingActivitySuggestionAdapter(
                    App.getContext(),
                    this.names,
                    this.information
            );

            list.setAdapter(relaxingActivityAdapter);
        } else {
            list.setVisibility(View.INVISIBLE);
        }
    }
}