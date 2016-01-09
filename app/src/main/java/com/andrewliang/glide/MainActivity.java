package com.andrewliang.glide;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

public class MainActivity extends Activity {

    private static MusicManager mM = new MusicManager();
    public static MusicManager getmM() {return mM;}
    private BroadcastReceiver mReceiver;

    @Override
    public void onBackPressed() {
        // exit the app when back button pressed
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    
    @Override
    protected void onResume()
    {
        if (ScreenReceiver.wasScreenOn) {       // Only start music if screen was on previously
            mM.start(this);   // Prevents music from playing during the lock screen after timeout
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        mM.pause();
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void finish() {
        super.finish();
        mM.release();
    }
}
