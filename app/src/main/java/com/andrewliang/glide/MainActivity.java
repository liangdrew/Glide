package com.andrewliang.glide;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;

public class MainActivity extends Activity {

    private static MusicManager mM = new MusicManager();
    public static MusicManager getmM() {return mM;}

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
        super.onResume();
        if (ScreenReceiver.wasScreenOn) {       // Only start music if screen was on previously
            mM.start(this);   // Prevents music from playing during the lock screen after timeout
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mM.pause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (mM != null) {
            mM.release();
            mM.start(this);
        }
    }

    @Override
    public void finish() {
        super.finish();
        mM.release();
    }

}
