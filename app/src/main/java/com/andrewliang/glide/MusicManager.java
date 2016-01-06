package com.andrewliang.glide;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

public class MusicManager {

    private static final String TAG = "MusicManager";

    private MediaPlayer mp;

    public void start(Context context) {

        if (mp == null) {
            mp = MediaPlayer.create(context, R.raw.beforedawn);
            mp.setVolume(0.5f, 0.5f);
            mp.setLooping(true);
            mp.start();
        }

        else if (!mp.isPlaying()) {
            mp.start();
        }
    }

    public void pause() {

        if (mp != null && mp.isPlaying()) {
            mp.pause();
        }
    }

    public void release() {
        Log.d(TAG, "Releasing media player");

        try {
            if (mp != null) {
                if (mp.isPlaying()) {
                    mp.stop();
                }
                mp.release();
                mp = null;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}