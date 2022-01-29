package com.example.sleepaid;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;

@SuppressLint("RestrictedApi")
public class SettingsScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_screen);

        BottomNavigationItemView settings = findViewById(R.id.settings);
        settings.setChecked(true);
    }

    public void showSleepData(MenuItem menuItem) {
        Intent sleepDataScreen = new Intent(this, SleepDataScreen.class);
        startActivity(sleepDataScreen);
    }

    public void showAlarms(MenuItem menuItem) {
        Intent alarmsScreen = new Intent(this, AlarmsScreen.class);
        startActivity(alarmsScreen);
    }

    public void showGoals(MenuItem menuItem) {
        Intent goalsScreen = new Intent(this, GoalsScreen.class);
        startActivity(goalsScreen);
    }

    public void showSettings(MenuItem menuItem) {}
}
