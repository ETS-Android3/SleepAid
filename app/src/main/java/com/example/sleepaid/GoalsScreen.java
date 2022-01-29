package com.example.sleepaid;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;

@SuppressLint("RestrictedApi")
public class GoalsScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals_screen);

        BottomNavigationItemView goals = findViewById(R.id.goals);
        goals.setChecked(true);
    }

    public void showSleepData(MenuItem menuItem) {
        Intent sleepDataScreen = new Intent(this, SleepDataScreen.class);
        startActivity(sleepDataScreen);
    }

    public void showGoals(MenuItem menuItem) {}

    public void showAlarms(MenuItem menuItem) {
        Intent alarmsScreen = new Intent(this, AlarmsScreen.class);
        startActivity(alarmsScreen);
    }

    public void showSettings(MenuItem menuItem) {
        Intent settingsScreen = new Intent(this, SettingsScreen.class);
        startActivity(settingsScreen);
    }
}
