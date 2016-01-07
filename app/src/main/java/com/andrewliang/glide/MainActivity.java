package com.andrewliang.glide;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import android.view.Display;

public class MainActivity extends Activity {

    private final String TAG = "MainActivity";
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
        mM.start(this);
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
