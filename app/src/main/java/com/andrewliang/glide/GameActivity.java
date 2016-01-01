package com.andrewliang.glide;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class GameActivity extends Activity
{
    private static final double DELTA_ANGLE = Math.PI/40;

    private boolean activityPaused = false;
    private boolean pauseButtonDown = false;

    // Pause "menu" buttons and text declaration
    private TextView highScoreText;
    private Button restartButton;
    private Button homeButton;
    private ImageView pausedTitle;

    // UI variables
    private GameView gameView;
    private ImageButton pauseButton;
    private ImageButton rightButton;
    private ImageButton leftButton;

    // High Scores data storage variables
    private static final String PREFS_NAME = "MyPrefsFile";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SharedPreferences getPrefs() {return this.prefs;}
    public SharedPreferences.Editor getEditor() {return this.editor;}

    @Override
    protected void onResume()
    {
        Log.d("GameActivity", "onResume");
        super.onResume();

        MainActivity.getmM().start(this);

        //resume the game loop
        if (activityPaused && !pauseButtonDown)
        {
            try
            {
                gameView.resume();
            }
            catch (Exception e)
            {
                Log.d("onResume", e.getMessage());
            }
            activityPaused = false;
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d("GameActivity", "onPause");

        MainActivity.getmM().pause();

        //pause the game loop
        if (!activityPaused)
        {
            try {gameView.pause();}
            catch (Exception e) {Log.d("onResume", e.getMessage());}
            activityPaused = true;
        }
    }

    @Override
    protected void onStop() // When user presses home button on device
    {
        super.onStop();
        Log.d("GameActivity", "onStop");

        // Have the user return to a paused app
//        gameView.pause();
//        activityPaused = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d("GameActivity", "onCreate");

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // requesting to turn the title OFF
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // making it full screen

        // HighScores
        prefs = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();

        FrameLayout gameLayout = new FrameLayout(this);
        gameView = new GameView(this, this);
        View buttonLayout = getLayoutInflater().inflate(R.layout.activity_game, null);

        gameLayout.addView(gameView);
        gameLayout.addView(buttonLayout);
        setContentView(gameLayout);

        highScoreText = (TextView) findViewById(R.id.new_high_score_text);
        pausedTitle = (ImageView) findViewById(R.id.pausedTitle);

        rightButton = (ImageButton) findViewById(R.id.right_button);

        //set the onClick listener to change the angle by some -dTheta during press, every 30 ms
        rightButton.setOnTouchListener(new View.OnTouchListener() {
            private Handler rightHandler;

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (rightHandler != null) return true;
                        rightHandler = new Handler();
                        rightHandler.postDelayed(TiltPlayerRight, 30);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (rightHandler == null) return true;
                        rightHandler.removeCallbacks(TiltPlayerRight);
                        rightHandler = null;
                        break;
                }
                return false;
            }

            //create the runnable for changing the angle of the player
            final Runnable TiltPlayerRight = new Runnable() {
                @Override
                public void run()
                {
                    gameView.getGameLoop().getPlayer().changeAngle(-DELTA_ANGLE);
                    rightHandler.postDelayed(this, 30);
                }
            };
        });

        leftButton = (ImageButton) findViewById(R.id.left_button);

        //set the onClick listener to change the angle by some +dTheta during press, every 30 ms
        leftButton.setOnTouchListener(new View.OnTouchListener() {
            private Handler leftHandler;

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (leftHandler != null) return true;
                        leftHandler = new Handler();
                        leftHandler.postDelayed(TiltPlayerLeft, 30);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (leftHandler == null) return true;
                        leftHandler.removeCallbacks(TiltPlayerLeft);
                        leftHandler = null;
                        break;
                }
                return false;
            }

            //create the runnable for changing the angle of the player
            final Runnable TiltPlayerLeft = new Runnable() {
                @Override
                public void run() {
                    gameView.getGameLoop().getPlayer().changeAngle(DELTA_ANGLE);
                    leftHandler.postDelayed(this, 30);
                }
            };

        });

        homeButton = (Button) findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.getmM().start(gameView.getGameActivity());
                setContentView(R.layout.activity_main);
            }
        });

        restartButton = (Button) findViewById(R.id.restart_button);
        restartButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {

                MainActivity.getmM().start(gameView.getGameActivity());
                highScoreText.setVisibility(TextView.GONE);
                pauseButtonDown = false;
                activityPaused = false;
                pausedTitle.setVisibility(View.GONE);
                pausedTitle.setImageResource(R.drawable.paused_title);
                pauseButton.setImageResource(R.drawable.pause_button);
                homeButton.setVisibility(Button.GONE);
                restartButton.setVisibility(Button.GONE);
                rightButton.setEnabled(true);
                leftButton.setEnabled(true);
                gameView.restartGame();

            }
        });

        pauseButton = (ImageButton) findViewById(R.id.pause_button);

        pauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //if paused, resume, otherwise pause
                if (activityPaused) {
                    rightButton.setEnabled(true);
                    leftButton.setEnabled(true);
                    pauseButton.setImageResource(R.drawable.pause_button);
                    gameView.resume();
                    pauseButtonDown = false;
                    pausedTitle.setVisibility(View.GONE);
                    restartButton.setVisibility(Button.GONE);
                    homeButton.setVisibility(Button.GONE);
                    MainActivity.getmM().start(gameView.getGameActivity());

                } else {
                    rightButton.setEnabled(false);
                    leftButton.setEnabled(false);
                    pauseButton.setImageResource(R.drawable.play_button_image);
                    gameView.pause();
                    pauseButtonDown = true;
                    pausedTitle.setVisibility(View.VISIBLE);
                    restartButton.setVisibility(Button.VISIBLE);
                    homeButton.setVisibility(Button.VISIBLE);
                    MainActivity.getmM().pause();
                }
                activityPaused = !activityPaused;       //invert the activity state

            }
        });
    }

    final Handler gameLoopMessageHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.arg1)
            {
                case 1:     //player death
                    die();
            }
        }
    };

    private void die()
    {
        if (gameView.getGameLoop().getPlayerScore() > 0) {   // Verify player has achieved a valid high score
            if (gameView.getGameLoop().getPlayerScore() > prefs.getInt("highScore", -999))  // Set new high score
            {
                gameView.getGameActivity().getEditor().putInt("highScore", gameView.getGameLoop().getPlayerScore());
                gameView.getGameActivity().getEditor().commit();
                highScoreText.setText("New high score: " + gameView.getGameLoop().getPlayerScore());
                highScoreText.setVisibility(TextView.VISIBLE);
            }
        }
        activityPaused = false;
        rightButton.setEnabled(false);
        leftButton.setEnabled(false);

        pausedTitle.setImageResource(R.drawable.gameover_title);
        pausedTitle.setVisibility(View.VISIBLE);
        homeButton.setVisibility(Button.VISIBLE);
        restartButton.setVisibility(Button.VISIBLE);

        pauseButtonDown = true;
        MainActivity.getmM().pause();
    }
}