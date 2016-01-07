package com.andrewliang.glide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
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
import java.lang.ref.WeakReference;

public class GameActivity extends Activity {
    private static final double DELTA_ANGLE = Math.PI/40;

    private boolean activityPaused = false;
    private boolean pauseButtonDown = false;

    // Pause "menu" buttons and text declaration
    private TextView highScoreText;
    private Button restartButton;
    private Button homeButton;
    private TextView pausedTitle;
    Paint pausedTitlePaint = new Paint();

    // UI variables
    private GameView gameView;
    private ImageButton pauseButton;
    private ImageButton rightButton;
    private ImageButton leftButton;
    private Handler leftHandler;
    private Handler rightHandler;

    // High Scores data storage variables
    private static final String PREFS_NAME = "MyPrefsFile";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    public SharedPreferences getPrefs() {return this.prefs;}
    public SharedPreferences.Editor getEditor() {return this.editor;}

    @Override
    protected void onResume() {
        super.onResume();

        MainActivity.getmM().start(this);
    }

    @Override
    protected void onPause() {

        //pause the game loop if the game is not already paused
        if (!activityPaused) {
            try {
                gameView.pause();
                rightButton.setEnabled(false);
                leftButton.setEnabled(false);
                pauseButton.setImageResource(R.drawable.play_button_image);

                homeButton.setVisibility(Button.VISIBLE);
                restartButton.setVisibility(Button.VISIBLE);
            } catch (Exception e) {
                Log.d("onPause", e.getMessage());

            }
            activityPaused = true;
        }
        super.onPause();
        MainActivity.getmM().pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // disable activity title and make full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // create the gameView object, and restore it to its previous state if needed
        Object data = getLastNonConfigurationInstance();
        if (data != null){
            gameView = new GameView(this, this);
            gameView.myOnRestoreInstanceState((Bundle) data);
        }
        else{
            gameView = new GameView(this, this);
        }

        // set up the activity layout
        final FrameLayout gameLayout = new FrameLayout(this);
        View buttonLayout = getLayoutInflater().inflate(R.layout.activity_game, null);
        gameLayout.addView(gameView);
        gameLayout.addView(buttonLayout);
        setContentView(gameLayout);

        // HighScores
        prefs = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
        highScoreText = (TextView) findViewById(R.id.new_high_score_text);
        pausedTitle = (TextView) findViewById(R.id.pausedTitle);
        pausedTitlePaint.setColor(Color.BLACK);
        pausedTitlePaint.setAntiAlias(true);
        pausedTitlePaint.setTypeface(Typeface.MONOSPACE);
        pausedTitlePaint.setTextSize(gameView.getViewWidth()/10);
        pausedTitle.getPaint().set(pausedTitlePaint);

        // create buttons and define their actions
        rightButton = (ImageButton) findViewById(R.id.right_button);
        leftButton = (ImageButton) findViewById(R.id.left_button);
        homeButton = (Button) findViewById(R.id.home_button);
        restartButton = (Button) findViewById(R.id.restart_button);
        pauseButton = (ImageButton) findViewById(R.id.pause_button);
        setButtonActions();
    }

    // the handler for communicating with the game loop
    public static class gameLoopMessageHandler extends Handler {
        private final WeakReference<GameActivity> gameActivityWeakReference;

        public gameLoopMessageHandler(GameActivity activity) {
            gameActivityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            GameActivity g = gameActivityWeakReference.get();
            if (g != null) g.die();
        }
    }

    private final gameLoopMessageHandler myGameLoopHandler = new gameLoopMessageHandler(this);
    public gameLoopMessageHandler getMyGameLoopHandler(){return myGameLoopHandler;}

    //create the runnables for changing the angle of the player
    final Runnable TiltPlayerRight = new Runnable() {
        @Override
        public void run() {
            gameView.getGameLoop().getPlayer().changeAngle(-DELTA_ANGLE);
            rightHandler.postDelayed(this, 30);
        }
    };

    //create the runnable for changing the angle of the player
    final Runnable TiltPlayerLeft = new Runnable() {
        @Override
        public void run() {
            gameView.getGameLoop().getPlayer().changeAngle(DELTA_ANGLE);
            leftHandler.postDelayed(this, 30);
        }
    };

    private void setButtonActions(){

        //set the onClick listener to change the angle by some -dTheta during press, every 30 ms
        rightButton.setOnTouchListener(new View.OnTouchListener() {

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

        });

        //set the onClick listener to change the angle by some +dTheta during press, every 30 ms
        leftButton.setOnTouchListener(new View.OnTouchListener() {

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
        });

        homeButton = (Button) findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.getmM().start(gameView.getGameActivity());
                setContentView(R.layout.activity_main);
            }
        });

        restartButton = (Button) findViewById(R.id.restart_button);
        restartButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                MainActivity.getmM().start(gameView.getGameActivity());

                highScoreText.setVisibility(TextView.GONE);
                pauseButtonDown = false;
                activityPaused = false;
                pausedTitle.setVisibility(View.GONE);
                pausedTitle.setText(R.string.pause_label);
                pauseButton.setImageResource(R.drawable.pause_button);
                homeButton.setVisibility(Button.GONE);
                restartButton.setVisibility(Button.GONE);
                rightButton.setEnabled(true);
                leftButton.setEnabled(true);
                if (rightHandler != null) rightHandler.removeCallbacks(TiltPlayerRight);
                if (leftHandler != null) leftHandler.removeCallbacks(TiltPlayerLeft);
                gameView.restartGame();

            }
        });

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
                Log.d("pause button", Boolean.toString(activityPaused));
                Log.d("game running", Boolean.toString(gameView.getGameLoop().gameIsRunning));
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(GameActivity.this, MainActivity.class));       //start the main activity
            }
        });
    }

    private void die()
    {
        if (gameView.getGameLoop().getPlayerScore() > 0) {   // Verify player has achieved a valid high score
            if (gameView.getGameLoop().getPlayerScore() > prefs.getInt("highScore", -999))  // Set new high score
            {
                this.getEditor().putInt("highScore", gameView.getGameLoop().getPlayerScore());
                this.getEditor().commit();
                highScoreText.setText("New high score: " + gameView.getGameLoop().getPlayerScore());
                highScoreText.setVisibility(TextView.VISIBLE);
            }
        }
        activityPaused = false;
        rightButton.setEnabled(false);
        leftButton.setEnabled(false);
        pausedTitle.setText(R.string.game_over_label);
        pausedTitle.setVisibility(View.VISIBLE);
        homeButton.setVisibility(Button.VISIBLE);
        restartButton.setVisibility(Button.VISIBLE);

        pauseButtonDown = true;
        MainActivity.getmM().pause();
    }

    @Override
    public Object onRetainNonConfigurationInstance(){
        Bundle ret =  gameView.myOnSaveInstanceState();
        gameView = null;
        return ret;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}