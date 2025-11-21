package com.cominatyou.batterytile.standalone;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.widget.Toast;

public class LockTileService extends TileService {

    // Variable to track the last time you clicked
    private long lastClickTime = 0;
    // Required cooldown in milliseconds (prevent spam crashes)
    private static final long CLICK_COOLDOWN = 500; 

    @Override
    public void onStartListening() {
        Tile tile = getQsTile();
        if (tile == null) return;

        tile.setLabel("Lock Screen");
        
        // Check if the service is ON or OFF
        boolean isEnabled = TileAccessibilityService.isServiceEnabled(this);
        
        // If OFF, show as Inactive (greyed out)
        tile.setState(isEnabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        
        tile.setIcon(Icon.createWithResource(this, R.drawable.ic_lock));
        tile.updateTile();
    }

    @Override
    public void onClick() {
        // 1. The Spam Filter: If clicked too fast, ignore it.
        if (SystemClock.elapsedRealtime() - lastClickTime < CLICK_COOLDOWN) {
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();

        if (TileAccessibilityService.isServiceEnabled(this)) {
            // It's ON: Lock the screen
            Intent intent = new Intent(this, TileAccessibilityService.class);
            intent.setAction(TileAccessibilityService.ACTION_LOCK_SCREEN);
            startService(intent);
        } else {
            // It's OFF: Tell the user and open Settings
            Toast.makeText(this, "Please enable 'Tile Toolkit' in Accessibility Settings", Toast.LENGTH_LONG).show();
            
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            try {
                // FIX: Android 14+ requires PendingIntent to open activities from tiles
                if (Build.VERSION.SDK_INT >= 34) {
                    PendingIntent pendingIntent = PendingIntent.getActivity(
                        this, 
                        0, 
                        intent, 
                        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                    );
                    startActivityAndCollapse(pendingIntent);
                } else {
                    // Older Android versions
                    startActivityAndCollapse(intent);
                }
            } catch (Exception e) {
                // If something goes wrong opening settings, don't crash the app. Just log it.
                Log.e("LockTileService", "Failed to launch settings", e);
                Toast.makeText(this, "Could not open settings.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
