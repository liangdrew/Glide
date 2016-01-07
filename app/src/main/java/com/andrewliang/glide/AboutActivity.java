package com.andrewliang.glide;

import android.app.Activity;
import android.os.Bundle;

public class AboutActivity extends Activity {

    @Override
    protected void onResume() {
        super.onResume();
        if (ScreenReceiver.wasScreenOn) {       // Only start music if screen was on previously
            MainActivity.getmM().start(this);   // Prevents music from playing during the lock screen after timeout
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.getmM().pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_about);
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}