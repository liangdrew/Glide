package com.andrewliang.glide;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

    private MediaPlayer mMediaPlayer;

    @Override
    public void onBackPressed(){
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
        Log.d("MainActivity", "onResume");

        mMediaPlayer = MediaPlayer.create(this, R.raw.a_guy_1_epicbuilduploop);
        mMediaPlayer.setVolume(0.5f, 0.5f);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d("MainActivity", "onPause");
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Log.d("MainActivity", "onStop");
        if (mMediaPlayer != null){
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
