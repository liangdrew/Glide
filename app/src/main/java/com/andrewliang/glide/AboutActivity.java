package com.andrewliang.glide;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class AboutActivity extends Activity {

    @Override
    protected void onResume() {
        super.onResume();
        MainActivity.getmM().start(this);
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