package com.andrewliang.glide;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Handle buttons here...
        View aboutButton = rootView.findViewById(R.id.about_button);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent2);
            }
        });

        View playButton = rootView.findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GameActivity.class);
                startActivity(intent);
            }
        });

        Paint titlePaint = new Paint();
        titlePaint.setColor(Color.BLACK);
        titlePaint.setAntiAlias(true);
        titlePaint.setTextSize(125);
        titlePaint.setTypeface(Typeface.MONOSPACE);
        TextView title = (TextView) rootView.findViewById(R.id.title_text);
        title.getPaint().set(titlePaint);

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}

