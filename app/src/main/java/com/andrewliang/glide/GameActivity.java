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

public class GameActivity extends Activity {
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
        Log.d("GameActivity", "onResume");
        super.onResume();
        //mMediaPlayer = MediaPlayer.create(this, R.raw.frankum_loop001e);
        //mMediaPlayer.setLooping(true);
        //mMediaPlayer.start();

        //resume the game loop
        Log.d("onResume", Boolean.toString(activityPaused));
        Log.d("pauseButtonDown", Boolean.toString(pauseButtonDown));


        if (activityPaused){
            pauseButton.callOnClick();
        }

    }

    @Override
    protected void onPause()
    {
        Log.d("GameActivity", "onPause");
        super.onPause();
        //release the music resource
        mHandler.removeCallbacks(null);

        if (mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
        }

        //pause the game loop
        if (!activityPaused) {
            pauseButton.callOnClick();
        }
    }

    @Override
    protected void onStop() // When user presses home button on device
    {
        Log.d("GameActivity", "onStop");
        super.onStop();

        //release the music resource
        if (mMediaPlayer != null){
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d("GameActivity", "onCreate");
        super.onCreate(savedInstanceState);

        gameView = new GameView(this, this);
        Object data = getLastNonConfigurationInstance();
        if (data != null){
            gameView.myOnRestoreInstanceState((Bundle) data);
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE); // requesting to turn the title OFF
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // making it full screen

        // HighScores
        prefs = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();

        final FrameLayout gameLayout = new FrameLayout(this);
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

                highScoreText.setVisibility(TextView.GONE);
                pauseButtonDown = false;
                activityPaused = false;
                Log.d("restart button", Boolean.toString(activityPaused));
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
                Log.d("pause button", Boolean.toString(activityPaused));
                Log.d("game running", Boolean.toString(gameView.getGameLoop().gameIsRunning));
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
        if (gameView.getGameLoop().getPlayerScore() > prefs.getInt("highScore", -999))  // Set new high score
        {
            this.getEditor().putInt("highScore", gameView.getGameLoop().getPlayerScore());
            this.getEditor().commit();
            highScoreText.setText("New high score: " + gameView.getGameLoop().getPlayerScore());
            highScoreText.setVisibility(TextView.VISIBLE);
        }
        activityPaused = false;
        Log.d("onResume", Boolean.toString(activityPaused));
        rightButton.setEnabled(false);
        leftButton.setEnabled(false);

        String gameOverText = "Game Over";
        pauseText.setText(gameOverText);
        pauseText.setVisibility(TextView.VISIBLE);
        homeButton.setVisibility(Button.VISIBLE);
        restartButton.setVisibility(Button.VISIBLE);

        pauseButtonDown = true;
    }

    @Override
    public Object onRetainNonConfigurationInstance(){
        Log.d("GameActivity", "onRetainNonConfigurationInstance");
        Bundle ret =  gameView.myOnSaveInstanceState();
        gameView = null;
        return ret;
    }

    @Override
    protected void onDestroy(){
        Log.d("GameActivity", "onDestroy");
        super.onDestroy();
    }
}