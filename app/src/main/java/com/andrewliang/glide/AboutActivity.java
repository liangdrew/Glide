package com.andrewliang.glide;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;


public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout aboutLayout = new FrameLayout(this);
        View aboutViewLayout = getLayoutInflater().inflate(R.layout.fragment_about, null);
        TextView aboutTitle = (TextView) findViewById(R.id.about_title);
        TextView aboutText = (TextView) findViewById(R.id.about_text);

//        aboutTitle.getPaint().setAntiAlias(true);
//        aboutText.getPaint().setAntiAlias(true);

        aboutLayout.addView(aboutViewLayout);

//        setContentView(R.layout.activity_high_scores);
        setContentView(aboutLayout);
    }
}