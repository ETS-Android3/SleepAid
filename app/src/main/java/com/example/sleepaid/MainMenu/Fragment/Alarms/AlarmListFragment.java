package com.example.sleepaid.MainMenu.Fragment.Alarms;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sleepaid.AlarmAdapter;
import com.example.sleepaid.App;
import com.example.sleepaid.Database.Alarm.Alarm;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Model.SharedViewModel;
import com.example.sleepaid.R;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@SuppressLint("NewApi")
public abstract class AlarmListFragment extends Fragment {
    private AppDatabase db;

    private SharedViewModel model;

    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        db = AppDatabase.getDatabase(App.getContext());

        model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    protected void loadAlarmList(int alarmType) {
        model.setAlarmViewType(alarmType);

        if (model.getAlarmListModel(alarmType) == null) {
            db.alarmDao()
                    .loadAllByTypes(new int[]{alarmType})
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            alarmData -> {
                                Collections.sort(alarmData);
                                model.setAlarms(alarmType, alarmData);

                                fillListView(alarmData);
                            },
                            Throwable::printStackTrace
                    );
        } else {
            fillListView(model.getAlarmList(alarmType));
        }
    }

    private void fillListView(List<Alarm> alarmList) {
        ListView list = getView().findViewById(R.id.alarmList);

        if (alarmList.size() != 0) {
            list.setVisibility(View.VISIBLE);

            AlarmAdapter alarmAdapter = new AlarmAdapter(
                    App.getContext(),
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
}
