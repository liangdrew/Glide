package com.andrewliang.glide;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class GameView extends SurfaceView implements SurfaceHolder.Callback
{
    private final SurfaceHolder s;
    private GameLoop gameLoop;
    private Canvas canvas;
    private final Paint BACKGROUND_PAINT = new Paint();
    private final Paint TEXT_PAINT = new Paint();
    private Thread gameThread;
    private final GameActivity gameActivity;
    private Player playerToDraw;
    private ArrayList<Food> foodsToDraw;
    private String scoreToDraw;
    private String livesToDraw;
    private String highScoreToDraw;

    private int viewHeight2;
    private int viewWidth2;

    public GameView(Context context, GameActivity a)
    {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        s = this.getHolder();
        this.gameActivity = a;
    }

    public GameLoop getGameLoop() {return this.gameLoop;}
    public GameActivity getGameActivity() {return this.gameActivity;}

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        //start the game loop in this method because we can actually get the screen
        // dimensions by the time this method is called, which is needed for gameloop
        super.onSizeChanged(w, h, oldw, oldh);

        gameLoop = new GameLoop(this);
        viewHeight2 = gameLoop.getViewHeight();
        viewWidth2 = gameLoop.getViewWidth();
        TEXT_PAINT.setColor(Color.BLACK);
        TEXT_PAINT.setAntiAlias(true);
        TEXT_PAINT.setTextSize(viewWidth2 / 25);  // Consider making text size constant
        BACKGROUND_PAINT.setColor(Color.WHITE);
        gameLoop.setGameIsRunning(true);
        gameThread = new Thread(gameLoop);
        gameThread.start();
    }

    final Handler drawingHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            //the message's object should be the player, so set the player that we will draw
            // (drawPlayer) as the object
            Box<ArrayList<Food>,Player,String, String, String> thingsToDraw = (Box)msg.obj;
            playerToDraw = thingsToDraw.getPlayer();
            foodsToDraw = thingsToDraw.getFoods();
            scoreToDraw = thingsToDraw.getScoreString();
            livesToDraw = thingsToDraw.getLivesString();
            highScoreToDraw = thingsToDraw.getHighScoreString();

            drawGame();
        }
    };

    public void pause()
    {
        gameLoop.setGameIsRunning(false);

        try {gameThread.join();} // Freezes thread
        catch (InterruptedException e){}
        gameThread = null;
    }

    public void resume()
    {
        gameLoop.setGameIsRunning(true);
        gameThread = new Thread(gameLoop);
        gameThread.start();
    }

    public void restartGame()
    {
        gameLoop = null;
        gameThread = null;

        gameLoop = new GameLoop(this);
        gameLoop.setGameIsRunning(true);
        gameThread = new Thread(gameLoop);
        gameThread.start();
    }


    private void drawGame()
    {
        //drawing sequence
        canvas = null;                                      //clear the canvas
        canvas = s.lockCanvas(null);                        //set the canvas to the canvas we can draw returned from lockCanvas()
        canvas.drawPaint(BACKGROUND_PAINT);

        playerToDraw.drawPlayer(canvas);                     //draw the player
        for (Food f:foodsToDraw) {f.drawFood(canvas);}       //draw the foods

        canvas.drawText(scoreToDraw, viewWidth2 /20, viewHeight2 /10, TEXT_PAINT); //draw the score text
        canvas.drawText(livesToDraw, viewWidth2 /3, viewHeight2 /10, TEXT_PAINT);
        canvas.drawText(highScoreToDraw, viewWidth2/2 + viewWidth2/15, viewHeight2/10, TEXT_PAINT);

        s.unlockCanvasAndPost(canvas);                       //finish drawing - stop editing the canvas and post it to the view
    }

    //override some interface methods because we implemented the SurfaceHolder.callback interface
    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        if (canvas != null)
        {
            drawGame();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){}
}


