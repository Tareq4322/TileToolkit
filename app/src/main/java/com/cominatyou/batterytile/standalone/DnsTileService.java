package com.cominatyou.batterytile.standalone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

public class DnsTileService extends TileService {

    // The hidden settings keys Android uses for Private DNS
    private static final String PRIVATE_DNS_MODE = "private_dns_mode";
    private static final String PRIVATE_DNS_SPECIFIER = "private_dns_specifier";

    @Override
    public void onStartListening() {
        updateTile();
    }

    private void updateTile() {
        Tile tile = getQsTile();
        if (tile == null) return;

        tile.setLabel("Private DNS");
        tile.setIcon(Icon.createWithResource(this, R.drawable.ic_dns));

        // Read the current state
        // Mode can be: "off", "opportunistic" (Automatic), or "hostname" (Private Provider name)
        String mode = Settings.Global.getString(getContentResolver(), PRIVATE_DNS_MODE);

        if ("hostname".equals(mode)) {
            // If on "hostname", show the provider name (e.g., "dns.google")
            String provider = Settings.Global.getString(getContentResolver(), PRIVATE_DNS_SPECIFIER);
            tile.setState(Tile.STATE_ACTIVE);
            tile.setSubtitle(provider != null ? provider : "On");
        } else if ("opportunistic".equals(mode)) {
            tile.setState(Tile.STATE_ACTIVE);
            tile.setSubtitle("Automatic");
        } else {
            tile.setState(Tile.STATE_INACTIVE);
            tile.setSubtitle("Off");
        }
        
        tile.updateTile();
    }

    @Override
    public void onClick() {
        // 1. Check for the magic ADB permission
        if (checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Missing! Run ADB command:", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "adb shell pm grant " + getPackageName() + " android.permission.WRITE_SECURE_SETTINGS", Toast.LENGTH_LONG).show();
            return;
        }

        // 2. Toggle Logic
        String mode = Settings.Global.getString(getContentResolver(), PRIVATE_DNS_MODE);
        
        if ("hostname".equals(mode) || "opportunistic".equals(mode)) {
            // If it's ON, turn it OFF
            Settings.Global.putString(getContentResolver(), PRIVATE_DNS_MODE, "off");
        } else {
            // If it's OFF, turn it ON
            // We prefer "hostname" (Specific Provider) if one is saved.
            String specifier = Settings.Global.getString(getContentResolver(), PRIVATE_DNS_SPECIFIER);
            if (specifier != null && !specifier.isEmpty()) {
                Settings.Global.putString(getContentResolver(), PRIVATE_DNS_MODE, "hostname");
            } else {
                // Fallback to Automatic if no provider is saved
                Settings.Global.putString(getContentResolver(), PRIVATE_DNS_MODE, "opportunistic");
            }
        }
        
        // Refresh the UI immediately
        updateTile();
    }
}
