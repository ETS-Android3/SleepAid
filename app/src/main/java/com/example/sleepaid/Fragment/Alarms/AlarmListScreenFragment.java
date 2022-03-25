package com.example.sleepaid.Fragment.Alarms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.sleepaid.App;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Fragment.MainMenuFragment;
import com.example.sleepaid.Model.SharedViewModel;
import com.example.sleepaid.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AlarmListScreenFragment extends MainMenuFragment implements View.OnClickListener {
    private AppDatabase db;

    private SharedViewModel model;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alarm_list_screen, container, false);
    }

    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        this.db = AppDatabase.getDatabase(App.getContext());

        this.model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        FloatingActionButton addAlarmButton = view.findViewById(R.id.addAlarmButton);
        addAlarmButton.setOnClickListener(this);

        NavHostFragment navHostFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.alarmList);

        NavController navController = navHostFragment.getNavController();
        BottomNavigationView bottomMenu = view.findViewById(R.id.alarmBottomMenu);

        NavigationUI.setupWithNavController(bottomMenu, navController);

        db.configurationDao()
                .loadAllByNames(new String[]{"supportNaps"})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        napData -> {
                            if (napData.get(0).getValue().equals("No.")) {
                                bottomMenu.getMenu().removeItem(R.id.napAlarmsFragment);
                            }
                        },
                        Throwable::printStackTrace
                );
    }

    public void onClick(View view) {
        this.model.setSelectedAlarm(null);

        NavHostFragment.findNavController(this).navigate(R.id.configureAlarmAction);
    }
}