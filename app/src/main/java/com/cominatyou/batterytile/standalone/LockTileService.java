package com.cominatyou.batterytile.standalone;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

public class LockTileService extends TileService {

    @Override
    public void onStartListening() {
        Tile tile = getQsTile();
        if (tile == null) return;

        tile.setLabel("Lock Screen");
        
        // Visual feedback: Active if permission granted, Inactive if not
        boolean isEnabled = TileAccessibilityService.isServiceEnabled(this);
        tile.setState(isEnabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        
        // Uses the ic_lock.xml icon we created earlier
        tile.setIcon(Icon.createWithResource(this, R.drawable.ic_lock));
        
        tile.updateTile();
    }

    @Override
    public void onClick() {
        if (TileAccessibilityService.isServiceEnabled(this)) {
            // Permission granted? Lock it.
            Intent intent = new Intent(this, TileAccessibilityService.class);
            intent.setAction(TileAccessibilityService.ACTION_LOCK_SCREEN);
            startService(intent);
        } else {
            // Permission missing? Send user to settings.
            Toast.makeText(this, "Please enable 'Battery Tile' in Accessibility Settings", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityAndCollapse(intent);
        }
    }
}
