package com.cominatyou.batterytile.standalone;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.service.quicksettings.TileService;
import android.widget.Toast;

// We only import the services that actually exist
import com.cominatyou.batterytile.standalone.DnsTileService;
import com.cominatyou.batterytile.standalone.LockTileService;
import com.cominatyou.batterytile.standalone.QuickSettingsTileService;
import com.cominatyou.batterytile.standalone.VolumeTileService;

public class QuickSettingsTileLongPressHandler extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the component name of the tile that triggered this activity
        ComponentName componentName = getIntent().getParcelableExtra(Intent.EXTRA_COMPONENT_NAME);

        if (componentName == null) {
            launchAppSettings();
            finish();
            return;
        }

        String className = componentName.getClassName();
        Intent targetIntent = null;

        // --- ROUTING LOGIC ---

        // 1. Battery Tile -> System Battery Settings
        if (className.equals(QuickSettingsTileService.class.getName())) {
            targetIntent = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
        }

        // 2. Volume Tile -> System Sound Settings
        else if (className.equals(VolumeTileService.class.getName())) {
            targetIntent = new Intent(Settings.ACTION_SOUND_SETTINGS);
        }

        // 3. DNS Tile -> System Network Settings
        else if (className.equals(DnsTileService.class.getName())) {
            // Try the specific Network & Internet page first
            targetIntent = new Intent("android.settings.NETWORK_AND_INTERNET_SETTINGS");
        }

        // 4. Lock Screen Tile -> This App's Settings
        else if (className.equals(LockTileService.class.getName())) {
            launchAppSettings();
            finish();
            return;
        }

        // --- EXECUTE ---

        if (targetIntent != null) {
            try {
                // Try to launch the specific settings page
                targetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(targetIntent);
            } catch (Exception e) {
                // ERROR HANDLER: If the specific page doesn't exist, fallback
                if (className.equals(DnsTileService.class.getName())) {
                    try {
                        // Fallback for DNS: Wireless Settings (Universally available)
                        Intent fallback = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(fallback);
                    } catch (Exception ex) {
                        Toast.makeText(this, "Could not find Network Settings", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Fallback for others: Main Settings
                    try {
                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                    } catch (Exception ex2) {
                        launchAppSettings();
                    }
                }
            }
        } else {
            launchAppSettings();
        }

        finish();
    }

    // New Launch Method: Uses the Intent Action defined in Manifest
    // This is safer than trying to import the class directly
    private void launchAppSettings() {
        try {
            Intent intent = new Intent("android.intent.action.APPLICATION_PREFERENCES");
            intent.setPackage(getPackageName()); // Ensure it opens THIS app's settings
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } catch (Exception e) {
            // Absolute worst case: try to find it by package name
            try {
                Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                if (intent != null) startActivity(intent);
            } catch (Exception ex) {
                Toast.makeText(this, "Could not open App Settings", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
