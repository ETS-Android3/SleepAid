package com.example.sleepaid;

import android.app.Application;
import android.content.Context;

public class App extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        this.context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
