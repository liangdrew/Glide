package com.andrewliang.glide;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


public class AboutActivity extends Activity {

    private AboutView aboutView;
    private Paint ABOUT_TEXT_PAINT = new Paint();

    public Point getSize() {

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE); // requesting to turn the title OFF
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//
//        ABOUT_TEXT_PAINT.setColor(Color.BLACK);
//        ABOUT_TEXT_PAINT.setAntiAlias(true);
//        ABOUT_TEXT_PAINT.setTextSize(getSize().x / 25);

//        FrameLayout aboutLayout = new FrameLayout(this);
//        aboutView = new AboutView(this, this);
//        View aboutViewLayout = getLayoutInflater().inflate(R.layout.fragment_about, null);
//        TextView aboutTitle = (TextView) findViewById(R.id.about_title);
//        TextView aboutText = (TextView) findViewById(R.id.about_text);
//        aboutTitle.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
//        aboutText.getPaint().setAntiAlias(true);

//        aboutLayout.addView(aboutView);
//        aboutLayout.addView(aboutViewLayout);

        setContentView(R.layout.activity_high_scores);
//        setContentView(aboutLayout);
    }
}