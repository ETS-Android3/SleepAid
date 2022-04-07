package com.example.sleepaid.Service.BlueLightFilter;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.core.app.NotificationCompat;

import com.example.sleepaid.App;
import com.example.sleepaid.R;

public class BlueLightFilterService extends Service {
    private View mOverlayView;
    private static boolean isRunning;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_DIM_BEHIND |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);

        // An alpha value to apply to this entire window.
        // An alpha of 1.0 means fully opaque and 0.0 means fully transparent
        params.alpha = 0.1F;

        // When FLAG_DIM_BEHIND is set, this is the amount of dimming to apply.
        // Range is from 1.0 for completely opaque to 0.0 for no dim.
        params.dimAmount = 0.2F;

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mOverlayView = inflater.inflate(R.layout.blue_light_filter, null);

        wm.addView(mOverlayView, params);

        Notification notification = new NotificationCompat.Builder(this, App.NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Blue light filter is on")
                .setContentText("It will remain on until the morning.")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.sleep_aid_circle)
                .setAutoCancel(false)
                .setOngoing(true)
                .build();

        startForeground(1930, notification);
        isRunning = true;

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        wm.removeView(mOverlayView);
    }

    public static boolean isRunning() {
        return isRunning;
    }

    public static void setIsRunning(boolean isRunning) {
        BlueLightFilterService.isRunning = isRunning;
    }
}