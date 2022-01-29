package com.example.sleepaid;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;

@SuppressLint("RestrictedApi")
public class SleepDataScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_data_screen);

        BottomNavigationItemView sleepData = findViewById(R.id.sleepData);
        sleepData.setChecked(true);
    }

    public void showSleepData(MenuItem menuItem) {}

    public void showAlarms(MenuItem menuItem) {
        Intent alarmsScreen = new Intent(this, AlarmsScreen.class);
        startActivity(alarmsScreen);
    }

    public void showGoals(MenuItem menuItem) {
        Intent goalsScreen = new Intent(this, GoalsScreen.class);
        startActivity(goalsScreen);
    }

    public void showSettings(MenuItem menuItem) {
        Intent settingsScreen = new Intent(this, SettingsScreen.class);
        startActivity(settingsScreen);
    }
}
