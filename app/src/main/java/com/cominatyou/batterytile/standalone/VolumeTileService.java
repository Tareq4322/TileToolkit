package com.cominatyou.batterytile.standalone;

import android.content.Context;
import android.media.AudioManager;
import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class VolumeTileService extends TileService {

    @Override
    public void onStartListening() {
        Tile tile = getQsTile();
        if (tile == null) return;
        
        tile.setLabel("Volume");
        tile.setState(Tile.STATE_ACTIVE);
        tile.setIcon(Icon.createWithResource(this, R.drawable.ic_volume));
        tile.updateTile();
    }

    @Override
    public void onClick() {
        // pure and simple: just ask the system to show the slider.
        // No closing panels, no ghost activities, no drama.
        try {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.adjustVolume(AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
