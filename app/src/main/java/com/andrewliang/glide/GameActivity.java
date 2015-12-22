package com.andrewliang.glide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
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
import android.widget.TextView;

public class GameActivity extends Activity
{
    private static final double DELTA_ANGLE = Math.PI/40;
    private MediaPlayer mMediaPlayer;
    private final Handler mHandler = new Handler();
    private boolean activityPaused = false;
    private boolean pauseButtonDown = false;

    // Pause "menu" buttons and text declaration
    private TextView pauseText;
    private TextView highScoreText;
    private ImageButton restartButton;
    private ImageButton homeButton;

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
        super.onResume();
        mMediaPlayer = MediaPlayer.create(this, R.raw.frankum_loop001e);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();

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

        //release the music resource
        mHandler.removeCallbacks(null);
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();

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
        //release the music resource
        mMediaPlayer.release();
        mMediaPlayer = null;

        // Have the user return to a paused app
        //gameView.pause();
        //activityPaused = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

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

        pauseText = (TextView) findViewById(R.id.pause_text);

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

        //home button - returns to main menu (main activity)
        homeButton = (ImageButton) findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(GameActivity.this, MainActivity.class));       //start the main activity
            }
        });

        //restart button
        restartButton = (ImageButton) findViewById(R.id.restart_button);
        restartButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                pauseButtonDown = false;
                activityPaused = false;
                pauseButton.setImageDrawable(getResources().getDrawable(R.drawable.pause_button));
                pauseText.setVisibility(TextView.GONE);
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
                    pauseButton.setImageDrawable(getResources().getDrawable(R.drawable.pause_button));
                    gameView.resume();
                    pauseButtonDown = false;
                    pauseText.setVisibility(View.GONE);
                    restartButton.setVisibility(Button.GONE);
                    homeButton.setVisibility(Button.GONE);
                } else {
                    rightButton.setEnabled(false);
                    leftButton.setEnabled(false);
                    pauseButton.setImageDrawable(getResources().getDrawable(R.drawable.pause_button));
                    gameView.pause();
                    pauseButtonDown = true;
                    pauseText.setVisibility(View.VISIBLE);
                    restartButton.setVisibility(Button.VISIBLE);
                    homeButton.setVisibility(Button.VISIBLE);
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
        Log.d("GameActivity", "current Highscore:" + gameView.getGameLoop().getPlayerScore());
        Log.d("GameActivity", "prefs Highscore:" + prefs.getInt("highscore", 0));
        if (gameView.getGameLoop().getPlayerScore() > prefs.getInt("highscore", 0))
        {
            highScoreText.setText("New high score: " + gameView.getGameLoop().getPlayerScore());
        }
        activityPaused = false;
        rightButton.setEnabled(false);
        leftButton.setEnabled(false);

        String gameOverText = "Game Over";
        pauseText.setText(gameOverText);
        pauseText.setVisibility(TextView.VISIBLE);
        highScoreText.setVisibility(TextView.VISIBLE);
        homeButton.setVisibility(Button.VISIBLE);
        restartButton.setVisibility(Button.VISIBLE);

        pauseButtonDown = true;
    }
}