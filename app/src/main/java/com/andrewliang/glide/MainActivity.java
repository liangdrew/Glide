package com.andrewliang.glide;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

    private final String TAG = "MainActivity";

    private static MusicManager mM = new MusicManager();

    public static MusicManager getmM() {return mM;}

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume");
        mM.start(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d(TAG, "onPause");
        mM.pause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        if (mM != null) {
            mM.release();
            mM.start(this);
        }
    }

    @Override
    public void finish() {
        super.finish();
        Log.d(TAG, "finish");
        mM.release();
    }

}
