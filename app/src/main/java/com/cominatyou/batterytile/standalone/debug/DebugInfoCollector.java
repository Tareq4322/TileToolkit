package com.cominatyou.batterytile.standalone.debug;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AlertDialog;

import com.cominatyou.batterytile.standalone.BuildConfig;
import com.cominatyou.batterytile.standalone.R;

import java.util.Locale;

public class DebugInfoCollector implements Runnable {
    private final Context context;
    private final AlertDialog dialog;

    // FIX: Added constructor to accept Context and AlertDialog
    public DebugInfoCollector(Context context, AlertDialog dialog) {
        this.context = context;
        this.dialog = dialog;
    }

    @Override
    public void run() {
        try {
            StringBuilder sb = new StringBuilder();

            // 1. App & Device Info
            sb.append("App Version: ").append(BuildConfig.VERSION_NAME).append(" (").append(BuildConfig.VERSION_CODE).append(")\n");
            sb.append("Build Type: ").append(BuildConfig.BUILD_TYPE).append("\n");
            sb.append("Device: ").append(Build.MANUFACTURER).append(" ").append(Build.MODEL).append("\n");
            sb.append("Android: ").append(Build.VERSION.RELEASE).append(" (SDK ").append(Build.VERSION.SDK_INT).append(")\n\n");

            // 2. Battery Info
            IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, filter);

            if (batteryStatus != null) {
                int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                float pct = level * 100 / (float) scale;

                int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                int plugged = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                int voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
                int temperature = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);

                sb.append("--- Battery Stats ---\n");
                sb.append("Level: ").append(level).append("/").append(scale).append(" (").append(String.format(Locale.US, "%.1f%%", pct)).append(")\n");
                sb.append("Status: ").append(statusToString(status)).append("\n");
                sb.append("Source: ").append(pluggedToString(plugged)).append("\n");
                sb.append("Voltage: ").append(voltage).append(" mV\n");
                // Temp is in tenths of a degree
                sb.append("Temp: ").append(temperature / 10.0f).append("°C\n");
            } else {
                sb.append("Error: Could not read battery intent.\n");
            }

            // 3. Update the UI (Must be done on Main Thread)
            String result = sb.toString();
            new Handler(Looper.getMainLooper()).post(() -> dialog.setMessage(result));

        } catch (Exception e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(() -> dialog.setMessage("Error collecting info: " + e.getMessage()));
        }
    }

    private String statusToString(int status) {
        return switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING -> "Charging";
            case BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging";
            case BatteryManager.BATTERY_STATUS_FULL -> "Full";
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not Charging";
            default -> "Unknown (" + status + ")";
        };
    }

    private String pluggedToString(int plugged) {
        return switch (plugged) {
            case BatteryManager.BATTERY_PLUGGED_AC -> "AC";
            case BatteryManager.BATTERY_PLUGGED_USB -> "USB";
            case BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless";
            case 0 -> "Battery";
            default -> "Unknown (" + plugged + ")";
        };
    }
}
