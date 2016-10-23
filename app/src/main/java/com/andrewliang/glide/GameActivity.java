package com.andrewliang.glide;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
    private boolean backPressed = false;
    private BroadcastReceiver mReceiver;

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

    // Interface for accessing and modifying high scores data
    private static final String PREFS_NAME = "MyPrefsFile";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    public SharedPreferences getPrefs() {return this.prefs;}
    public SharedPreferences.Editor getEditor() {return this.editor;}

    @Override
    protected void onResume() {

        if (ScreenReceiver.wasScreenOn) {       // Only start music if screen was on previously
            MainActivity.getmM().start(this);   // Prevents music from playing during the lock screen after timeout
        }
        super.onResume();
    }

    @Override
    protected void onPause() {

        MainActivity.getmM().pause();

        // When screen is turning OFF
        //pause the game loop if the game is not already paused
        if (!backPressed) {
            homeButton.setVisibility(Button.VISIBLE);
            restartButton.setVisibility(Button.VISIBLE);
            backPressed = false;
            pauseButton.setImageResource(R.drawable.play_button_image);
            pausedTitle.setVisibility(View.VISIBLE);
        }
        if (!activityPaused) {
            try {
                gameView.pause();
                rightButton.setEnabled(false);
                leftButton.setEnabled(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            activityPaused = true;
        }
        super.onPause();
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

        // High scores
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
            if (g != null) { g.die(); }
        }
    }

    private final gameLoopMessageHandler myGameLoopHandler = new gameLoopMessageHandler(this);
    public gameLoopMessageHandler getMyGameLoopHandler() { return myGameLoopHandler; }

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

    private void setButtonActions() {

        //set the onClick listener to change the angle by some -dTheta during press, every 30 ms
        rightButton.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (rightHandler != null) return true;
                        rightHandler = new Handler();
                        rightHandler.postDelayed(TiltPlayerRight, 30);  // Add Runnable to the message queue
                        break;
                    case MotionEvent.ACTION_UP:
                        if (rightHandler == null) return true;
                        rightHandler.removeCallbacks(TiltPlayerRight);  // Remove pending posts of Runnable in queue
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

        restartButton = (Button) findViewById(R.id.restart_button);
        restartButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                MainActivity.getmM().start(gameView.getGameActivity());

                highScoreText.setVisibility(TextView.GONE);
                activityPaused = false;
                pausedTitle.setVisibility(View.GONE);
                pausedTitle.setText(R.string.pause_label);
                pauseButton.setImageResource(R.drawable.pause_button);
                homeButton.setVisibility(Button.GONE);
                restartButton.setVisibility(Button.GONE);
                rightButton.setEnabled(true);
                leftButton.setEnabled(true);
                if (rightHandler != null) { rightHandler.removeCallbacks(TiltPlayerRight); }
                if (leftHandler != null) { leftHandler.removeCallbacks(TiltPlayerLeft); }
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
                    pausedTitle.setVisibility(View.GONE);
                    restartButton.setVisibility(Button.GONE);
                    homeButton.setVisibility(Button.GONE);
                    MainActivity.getmM().start(gameView.getGameActivity());

                } else {
                    rightButton.setEnabled(false);
                    leftButton.setEnabled(false);
                    pauseButton.setImageResource(R.drawable.play_button_image);
                    gameView.pause();
                    pausedTitle.setVisibility(View.VISIBLE);
                    restartButton.setVisibility(Button.VISIBLE);
                    homeButton.setVisibility(Button.VISIBLE);
                    MainActivity.getmM().pause();
                }
                activityPaused = !activityPaused;       //invert the activity state
            }
        });

        homeButton = (Button) findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int pScore = gameView.getGameLoop().getPlayerScore();
                if(needToSetHighScore(pScore)) { saveHighScore(pScore); }
                MainActivity.getmM().start(gameView.getGameActivity());
                startActivity(new Intent(GameActivity.this, MainActivity.class));       //start the main activity
            }
        });
    }

    private void die() {

        int pScore = gameView.getGameLoop().getPlayerScore();
        if (needToSetHighScore(pScore)) {
            saveHighScore(pScore);
            highScoreText.setText("New high score: " + pScore);
            highScoreText.setVisibility(TextView.VISIBLE);
        }
        activityPaused = false;
        rightButton.setEnabled(false);
        leftButton.setEnabled(false);
        pausedTitle.setText(R.string.game_over_label);
        pausedTitle.setVisibility(View.VISIBLE);
        homeButton.setVisibility(Button.VISIBLE);
        restartButton.setVisibility(Button.VISIBLE);

        MainActivity.getmM().pause();
    }

    private boolean needToSetHighScore(int pScore) {
        // Verify if the player has achieved a valid high score
        if (pScore > 0 && pScore > prefs.getInt("highScore", -999)) { return true; }
        else { return false; }
    }

    private void saveHighScore(int pScore) {
        this.getEditor().putInt("highScore", pScore);
        this.getEditor().commit();
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        Bundle ret =  gameView.myOnSaveInstanceState();
        gameView = null;
        return ret;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backPressed = true;
    }
}
