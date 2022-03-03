package com.example.sleepaid.MainMenu.Fragment.Alarms;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sleepaid.AlarmAdapter;
import com.example.sleepaid.App;
import com.example.sleepaid.Database.Alarm.Alarm;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Model.SharedViewModel;
import com.example.sleepaid.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@SuppressLint("NewApi")
public abstract class AlarmListFragment extends Fragment implements AdapterView.OnItemClickListener {
    private AppDatabase db;

    private SharedViewModel model;

    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        this.db = AppDatabase.getDatabase(App.getContext());

        this.model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        ListView list = getView().findViewById(R.id.alarmList);
        list.setOnItemClickListener(this);
    }

    protected void loadAlarmList(int alarmType) {
        this.model.setAlarmViewType(alarmType);

        if (this.model.getAlarmListModel(alarmType) == null) {
            this.db.alarmDao()
                    .loadAllByTypes(new int[]{alarmType})
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            alarmData -> {
                                Collections.sort(alarmData);
                                this.model.setAlarms(alarmType, alarmData);

                                fillListView(alarmData);
                            },
                            Throwable::printStackTrace
                    );
        } else {
            fillListView(this.model.getAlarmList(alarmType));
        }
    }

    private void fillListView(List<Alarm> alarmList) {
        ListView list = getView().findViewById(R.id.alarmList);

        if (alarmList.size() != 0) {
            list.setVisibility(View.VISIBLE);

            AlarmAdapter alarmAdapter = new AlarmAdapter(
                    App.getContext(),
                    alarmList.stream().map(a -> alarmList.indexOf(a)).collect(Collectors.toList()),
                    alarmList.stream().map(Alarm::getTime).collect(Collectors.toList()),
                    alarmList.stream().map(Alarm::getDays).collect(Collectors.toList()),
                    getResources().getColor(R.color.purple_sleep),
                    getResources().getColor(R.color.black_transparent)
            );

            list.setAdapter(alarmAdapter);
        } else {
            list.setVisibility(View.INVISIBLE);
        }
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        this.model.setSelectedAlarm(this.model.getAlarmList(this.model.getAlarmViewType()).get(view.getId()));

        NavHostFragment.findNavController(this).navigate(R.id.configureAlarmAction);
    }
}
