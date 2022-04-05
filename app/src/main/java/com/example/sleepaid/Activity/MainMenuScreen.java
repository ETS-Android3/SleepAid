package com.example.sleepaid.Activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.sleepaid.Component.Modal;
import com.example.sleepaid.R;
import com.example.sleepaid.Service.BlueLightFilter.BlueLightFilterBroadcastReceiverService;
import com.example.sleepaid.Service.BlueLightFilter.BlueLightFilterService;
import com.google.android.material.navigation.NavigationView;

import java.time.ZonedDateTime;

public class MainMenuScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_menu_screen_host);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.content);
        NavController navController = navHostFragment.getNavController();

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navView = findViewById(R.id.navView);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.sleepDataFragment,
                R.id.alarmsFragment,
                R.id.goalsFragment,
                R.id.sleepDiaryFragment,
                R.id.relaxingActivitiesSuggestionsFragment,
                R.id.settingsFragment
        )
                .setOpenableLayout(drawerLayout)
                .build();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.main_menu_screen_graph);
        Bundle args = new Bundle();

        if (getIntent() != null && getIntent().hasExtra("PARENT_DESTINATION")) {
            navGraph.setStartDestination(getIntent().getIntExtra("PARENT_DESTINATION", R.id.sleepDataFragment));

            if(getIntent().hasExtra("DESTINATION")) {
                args.putInt("DESTINATION", getIntent().getIntExtra("DESTINATION", 0));
            } else {
                args = null;
            }
        } else {
            navGraph.setStartDestination(R.id.sleepDataFragment);
            args = null;
        }

        navController.setGraph(navGraph, args);

        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }
}
