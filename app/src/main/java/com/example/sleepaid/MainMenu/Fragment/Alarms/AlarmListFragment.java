package com.example.sleepaid.MainMenu.Fragment.Alarms;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sleepaid.AlarmAdapter;
import com.example.sleepaid.App;
import com.example.sleepaid.Database.Alarm.Alarm;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.ListMultiChoiceModeListener;
import com.example.sleepaid.Model.SharedViewModel;
import com.example.sleepaid.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@SuppressLint("NewApi")
public abstract class AlarmListFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private AppDatabase db;

    private SharedViewModel model;

    private AlarmListScreenFragment alarmListScreenFragment;

    private List<Alarm> alarmList;

    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        this.db = AppDatabase.getDatabase(App.getContext());

        this.model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        this.alarmListScreenFragment = (AlarmListScreenFragment) getParentFragment().getParentFragment();

        ListView list = getView().findViewById(R.id.alarmList);
        list.setOnItemClickListener(this);
        list.setOnItemLongClickListener(this);

        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
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
                                this.alarmList = alarmData;

                                fillListView(alarmData);
                            },
                            Throwable::printStackTrace
                    );
        } else {
            this.alarmList = this.model.getAlarmList(alarmType);
            fillListView(this.model.getAlarmList(alarmType));
        }
    }

    private void fillListView(List<Alarm> alarmList) {
        ListView list = getView().findViewById(R.id.alarmList);

        if (alarmList.size() != 0) {
            list.setVisibility(View.VISIBLE);

            AlarmAdapter alarmAdapter = new AlarmAdapter(
                    App.getContext(),
                    this,
                    alarmList.stream().map(a -> alarmList.indexOf(a)).collect(Collectors.toList()),
                    alarmList.stream().map(Alarm::getTime).collect(Collectors.toList()),
                    alarmList.stream().map(Alarm::getDays).collect(Collectors.toList()),
                    alarmList.stream().map(Alarm::getIsOn).collect(Collectors.toList()),
                    getResources().getColor(R.color.lavender_sleep_transparent),
                    getResources().getColor(R.color.purple_sleep),
                    getResources().getColor(R.color.black_transparent)
            );

            list.setAdapter(alarmAdapter);
            alarmAdapter.notifyDataSetChanged();

            list.setMultiChoiceModeListener(new ListMultiChoiceModeListener(this, alarmAdapter));
        } else {
            list.setVisibility(View.INVISIBLE);
        }
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        this.model.setSelectedAlarm(this.alarmList.get(position));

        NavHostFragment.findNavController(this.alarmListScreenFragment).navigate(R.id.configureAlarmAction);
    }

    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        ((ListView) view).setItemChecked(position, !((AlarmAdapter)adapterView.getAdapter()).isPositionChecked(position));
        return false;
    }

    public void toggleAlarms(List<Integer> selectedIds, int isOn) {
        List<Alarm> alarmsToSwitch = this.alarmList
                .stream()
                .filter(a -> selectedIds.contains(this.alarmList.indexOf(a)))
                .collect(Collectors.toList());

        alarmList.removeAll(alarmsToSwitch);

        for (Alarm a : alarmsToSwitch) {
            a.setIsOn(isOn);
        }

        this.db.alarmDao()
                .update(alarmsToSwitch)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            alarmList.addAll(alarmsToSwitch);
                            Collections.sort(alarmList);
                            this.model.setAlarms(model.getAlarmViewType(), alarmList);

                            this.fillListView(alarmList);
                        },
                        Throwable::printStackTrace
                );
    }

    public void deleteRows(List<Integer> selectedIds) {
        List<Alarm> alarmsToDelete = this.alarmList
                .stream()
                .filter(a -> selectedIds.contains(this.alarmList.indexOf(a)))
                .collect(Collectors.toList());

        this.db.alarmDao()
                .delete(alarmsToDelete)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            alarmList.removeAll(alarmsToDelete);
                            Collections.sort(alarmList);
                            this.model.setAlarms(model.getAlarmViewType(), alarmList);

                            this.fillListView(alarmList);
                        },
                        Throwable::printStackTrace
                );
    }
}
