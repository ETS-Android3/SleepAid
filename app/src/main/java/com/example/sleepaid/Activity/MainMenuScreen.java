package com.example.sleepaid.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.sleepaid.R;
import com.google.android.material.navigation.NavigationView;

@SuppressLint("RestrictedApi")
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
                R.id.settingsFragment
        )
                .setOpenableLayout(drawerLayout)
                .build();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }
}
