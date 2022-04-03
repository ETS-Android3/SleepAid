package com.example.sleepaid.Activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

@SuppressLint("RestrictedApi")
public class MainMenuScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_menu_screen_host);

        this.setupBlueLightFilter();

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

    @SuppressLint("NewApi")
    private void setupBlueLightFilter() {
        if (!Settings.canDrawOverlays(this)) {
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            boolean haveAskedForPermission = sharedPref.getBoolean("asked_blue_light_filter_permission", false);

            if (!haveAskedForPermission) {
                sharedPref = this.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("asked_blue_light_filter_permission", true);
                editor.apply();

                DialogInterface.OnClickListener yesAction = (dialog, whichButton) -> {
                    Intent overlayPermissionIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    startActivity(overlayPermissionIntent);
                };

                DialogInterface.OnClickListener noAction = (dialog, whichButton) -> {
                };

                Modal.show(
                        this,
                        getString(R.string.blue_light_filter_permission),
                        getString(R.string.yes_modal),
                        yesAction,
                        getString(R.string.no_modal),
                        noAction
                );
            }
        } else {
            AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

            if (alarmManager.canScheduleExactAlarms() && !BlueLightFilterService.isRunning()) {
                Intent startIntent = new Intent(this, BlueLightFilterBroadcastReceiverService.class);

                long startTime = ZonedDateTime.now()
                        .withHour(19)
                        .withMinute(30)
                        .toInstant()
                        .toEpochMilli();

                startIntent.putExtra("HOUR", 19);
                startIntent.putExtra("MINUTE", 30);

                PendingIntent startPendingIntent = PendingIntent.getBroadcast(this, (int) startTime, startIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

                alarmManager.setAlarmClock(
                        new AlarmManager.AlarmClockInfo(
                                Math.max(startTime, System.currentTimeMillis()),
                                startPendingIntent
                        ),
                        startPendingIntent
                );

                Intent stopIntent = new Intent(this, BlueLightFilterBroadcastReceiverService.class);

                long stopTime = ZonedDateTime.now()
                        .withHour(7)
                        .withMinute(30)
                        .plusDays(1)
                        .toInstant()
                        .toEpochMilli();

                stopIntent.setAction("STOP");

                PendingIntent stopPendingIntent = PendingIntent.getBroadcast(this, (int) stopTime, stopIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

                alarmManager.setAlarmClock(
                        new AlarmManager.AlarmClockInfo(
                                Math.max(stopTime, System.currentTimeMillis()),
                                stopPendingIntent
                        ),
                        stopPendingIntent
                );
            }
        }
    }
}
