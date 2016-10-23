package com.andrewliang.glide;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class AboutActivity extends Activity {

    @Override
    protected void onResume() {

        if (ScreenReceiver.wasScreenOn) {       // Only start music if screen was on previously
            MainActivity.getmM().start(this);   // Prevents music from playing during the lock screen after timeout
        }
        super.onResume();

    }

    @Override
    protected void onPause() {

        MainActivity.getmM().pause();
        super.onPause();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_about);
        
    }
}
