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

    private long lastClickTime = 0;
    private static final long CLICK_COOLDOWN = 500; 

    @Override
    public void onStartListening() {
        Tile tile = getQsTile();
        if (tile == null) return;
        tile.setLabel("Lock Screen");
        tile.setState(Tile.STATE_INACTIVE);
        tile.setIcon(Icon.createWithResource(this, R.drawable.ic_lock));
        tile.updateTile();
    }

    @Override
    public void onClick() {
        if (SystemClock.elapsedRealtime() - lastClickTime < CLICK_COOLDOWN) {
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();

        // --- NEW LOGIC START ---
        // Try to lock using the static instance directly
        boolean success = TileAccessibilityService.requestLock();
        
        // If success is false, it means the service is not running/enabled
        if (!success) {
            // Double check via settings (just in case)
            if (TileAccessibilityService.isServiceEnabled(this)) {
                // If settings say it's on, but 'instance' is null, the system killed it.
                // We can't force restart it easily, but usually this state is rare with the new code.
                Toast.makeText(this, "Service is restarting...", Toast.LENGTH_SHORT).show();
            } else {
                // Permission is definitely missing
                Toast.makeText(this, "Enable Accessibility for QS Toolkit", Toast.LENGTH_LONG).show();
                
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                try {
                    if (Build.VERSION.SDK_INT >= 34) {
                        PendingIntent pendingIntent = PendingIntent.getActivity(
                            this, 
                            0, 
                            intent, 
                            PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                        );
                        startActivityAndCollapse(pendingIntent);
                    } else {
                        startActivityAndCollapse(intent);
                    }
                } catch (Exception e) {
                    Log.e("LockTileService", "Failed to launch settings", e);
                }
            }
        }
        // --- NEW LOGIC END ---
    }
}
