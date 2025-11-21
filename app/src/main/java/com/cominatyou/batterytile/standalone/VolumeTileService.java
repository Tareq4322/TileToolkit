package com.cominatyou.batterytile.standalone;

import android.content.Context;
import android.media.AudioManager;
import android.graphics.drawable.Icon;
import android.os.Handler;
import android.os.Looper;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class VolumeTileService extends TileService {

    // Handler to manage the timing
    private final Handler handler = new Handler(Looper.getMainLooper());
    
    // The task that resets the tile to inactive
    private final Runnable resetRunnable = new Runnable() {
        @Override
        public void run() {
            Tile tile = getQsTile();
            if (tile != null) {
                tile.setState(Tile.STATE_INACTIVE);
                tile.updateTile();
            }
        }
    };

    @Override
    public void onStartListening() {
        Tile tile = getQsTile();
        if (tile == null) return;
        
        tile.setLabel("Volume");
        // Default state is Inactive (Grey)
        tile.setState(Tile.STATE_INACTIVE);
        tile.setIcon(Icon.createWithResource(this, R.drawable.ic_volume));
        tile.updateTile();
    }

    @Override
    public void onClick() {
        Tile tile = getQsTile();
        if (tile == null) return;

        // 1. Visual Feedback: Light it up!
        tile.setState(Tile.STATE_ACTIVE);
        tile.updateTile();

        // 2. Do the actual work (Show Volume Slider)
        try {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.adjustVolume(AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3. Schedule the reset: Turn it off after 2 seconds
        // Remove any pending resets first (so rapid clicks don't cause flickering)
        handler.removeCallbacks(resetRunnable);
        handler.postDelayed(resetRunnable, 2000);
    }
    
    @Override
    public void onStopListening() {
        // Clean up callbacks if the user closes the shade
        handler.removeCallbacks(resetRunnable);
    }
}
