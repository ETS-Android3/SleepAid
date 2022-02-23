package com.example.sleepaid.MainMenu.Fragment.Alarms;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sleepaid.App;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AlarmsFragment extends Fragment {
    private AppDatabase db;

    protected AlarmListFragment alarmListFragment;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alarms, container, false);
    }

    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        db = AppDatabase.getDatabase(App.getContext());

        NavHostFragment navHostFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.alarms);

        NavController navController = navHostFragment.getNavController();
        BottomNavigationView bottomMenu = getView().findViewById(R.id.bottomMenu);

        NavigationUI.setupWithNavController(bottomMenu, navController);

        db.configurationDao()
                .loadAllByTypes(new String[]{"supportNaps"})
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
}