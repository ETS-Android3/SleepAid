package com.example.sleepaid;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;

@SuppressLint("RestrictedApi")
public class AlarmsScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarms_screen);

        BottomNavigationItemView alarms = findViewById(R.id.alarms);
        alarms.setChecked(true);
    }

    public void showSleepData(MenuItem menuItem) {
        Intent sleepDataScreen = new Intent(this, SleepDataScreen.class);
        startActivity(sleepDataScreen);
    }

    public void showAlarms(MenuItem menuItem) {}

    public void showGoals(MenuItem menuItem) {
        Intent goalsScreen = new Intent(this, GoalsScreen.class);
        startActivity(goalsScreen);
    }

    public void showSettings(MenuItem menuItem) {
        Intent settingsScreen = new Intent(this, SettingsScreen.class);
        startActivity(settingsScreen);
    }
}
