package com.andrewliang.glide;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainFragment extends Fragment {

    private AlertDialog mDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // Handle buttons here...
        View aboutButton = rootView.findViewById(R.id.about_button);
        aboutButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent2 = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent2);

                /*AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.about_title);
                builder.setMessage(R.string.about_text);
                builder.setCancelable(false); // tells Android not to dismiss dialog when user taps outside box
                builder.setPositiveButton(R.string.ok_label,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface,
                                                int i) {
                                // nothing
                            }
                        });
                mDialog = builder.show();*/
            }
        });

        View playButton = rootView.findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GameActivity.class);
                startActivity(intent);
            }
        });

        /*View highScoresButton = rootView.findViewById(R.id.high_scores_button);
        highScoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent2);
            }
        });*/

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();

        // Get rid of the dialog if it's still up
        if (mDialog != null)
            mDialog.dismiss();
    }
}

