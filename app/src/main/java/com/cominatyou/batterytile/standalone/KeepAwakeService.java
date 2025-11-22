package com.cominatyou.batterytile.standalone;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;

import com.cominatyou.batterytile.standalone.R;

public class KeepAwakeService extends Service {

    private PowerManager.WakeLock wakeLock;
    public static boolean isRunning = false;
    private static final String CHANNEL_ID = "keep_awake_channel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 1. Start as Foreground (Required to hold a WakeLock long-term)
        startForeground(1, createNotification());

        // 2. Acquire the WakeLock
        if (wakeLock == null) {
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            // SCREEN_BRIGHT_WAKE_LOCK is deprecated but necessary for this specific use case 
            // (keeping screen on from a service without an activity)
            wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "QSToolkit:KeepAwake");
            wakeLock.acquire();
        }

        isRunning = true;
        
        // Notify the Tile to update its UI
        CaffeineTileService.requestUpdate(this);
        
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Release the lock so the screen can sleep again
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
        isRunning = false;
        CaffeineTileService.requestUpdate(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification createNotification() {
        return new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Caffeine is On")
                .setContentText("Keeping your screen awake...")
                .setSmallIcon(R.drawable.ic_coffee)
                .setOngoing(true)
                .build();
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Caffeine Service",
                NotificationManager.IMPORTANCE_LOW
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }
}
