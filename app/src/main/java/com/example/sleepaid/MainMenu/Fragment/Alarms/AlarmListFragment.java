package com.example.sleepaid.MainMenu.Fragment.Alarms;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sleepaid.AlarmAdapter;
import com.example.sleepaid.App;
import com.example.sleepaid.Database.Alarm.Alarm;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressLint("NewApi")
public abstract class AlarmListFragment extends Fragment {
    protected AlarmsFragment alarmsFragment;

    protected AppDatabase db;

    protected List<Alarm> alarmList;

    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        alarmsFragment = (AlarmsFragment) getParentFragment().getParentFragment();
        alarmsFragment.alarmListFragment = this;

        db = AppDatabase.getDatabase(App.getContext());

        loadAlarmList();
    }

    protected void loadAlarmList() {
        ListView list = getView().findViewById(R.id.alarmList);

        if (alarmList.size() != 0) {
            list.setVisibility(View.VISIBLE);

            List<String> alarmText = new ArrayList<>();

            for (Alarm a : alarmList) {
                alarmText.add(a.getTime() + "   " + a.getDays());
            }

            AlarmAdapter alarmAdapter = new AlarmAdapter(
                    App.getContext(),
                    alarmList.stream().map(Alarm::getTime).collect(Collectors.toList()),
                    alarmList.stream().map(Alarm::getDays).collect(Collectors.toList())
            );

            list.setAdapter(alarmAdapter);
        } else {
            list.setVisibility(View.INVISIBLE);
        }
    }
}
