package com.andrewliang.glide;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
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

    public GameView(Context context, GameActivity a) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        s = this.getHolder();
        this.gameActivity = a;

        // get screen dimensions
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        viewWidth2 = size.x;
        viewHeight2 = size.y;

        // set paints
        TEXT_PAINT.setColor(Color.BLACK);
        TEXT_PAINT.setAntiAlias(true);
        TEXT_PAINT.setTextSize(viewWidth2 / 25);
        BACKGROUND_PAINT.setColor(Color.WHITE);

        // create game loop and game thread, and start thread
        gameLoop = new GameLoop(this, viewWidth2, viewHeight2);
        gameLoop.setGameIsRunning(true);
        gameThread = new Thread(gameLoop);
        gameThread.start();

    }

    public Bundle myOnSaveInstanceState() {
        // save game data in a bundle
        Bundle bundle = new Bundle();

        // save player data
        bundle.putInt("xPos", gameLoop.getPlayer().getxPos());
        bundle.putInt("yPos", gameLoop.getPlayer().getyPos());
        bundle.putInt("playerLives", gameLoop.getPlayerLives());
        bundle.putInt("playerScore", gameLoop.getPlayerScore());
        bundle.putDouble("player angle", gameLoop.getPlayer().getAngle());
        bundle.putDouble("player velocity", gameLoop.getPlayer().getVelocity());

        // save the food data
        ArrayList<int[]> foodData = gameLoop.getFoodData();
        bundle.putIntArray("food xPos", foodData.get(0));
        bundle.putIntArray("food yPos", foodData.get(1));
        bundle.putIntArray("food speeds", foodData.get(2));
        bundle.putIntArray("food types", foodData.get(3));

        return bundle;
    }

    public void myOnRestoreInstanceState(Bundle bundle) {
        // load game data from previous state stored in bundle

        // load player data
        gameLoop.setPlayerScore(bundle.getInt("playerScore"));
        gameLoop.setPlayerLives(bundle.getInt("playerLives"));
        Player p = new Player(viewWidth2, viewHeight2, bundle.getInt("xPos"), bundle.getInt("yPos"));
        p.setAngle(bundle.getDouble("player angle"));
        p.setVelocity(bundle.getDouble("player velocity"));
        gameLoop.setPlayer(p);

        // load food data
        gameLoop.setFoods(bundle.getIntArray("food xPos"), bundle.getIntArray("food yPos"),
                bundle.getIntArray("food speeds"), bundle.getIntArray("food types"));
    }

    public GameLoop getGameLoop() {return this.gameLoop;}
    public GameActivity getGameActivity() {return this.gameActivity;}
    public int getViewWidth() {return this.viewWidth2;}

    // handler for receiving draw data from the game loop thread
    public static class drawingHandler extends Handler{
        private final WeakReference<GameView> gameViewWeakReference;

        public drawingHandler(GameView g){
            gameViewWeakReference = new WeakReference<>(g);
        }

        @Override
        public void handleMessage(Message msg){
            GameView g = gameViewWeakReference.get();
            Box<ArrayList<Food>,Player,String, String, String> thingsToDraw = (Box)msg.obj;
            g.playerToDraw = thingsToDraw.getPlayer();
            g.foodsToDraw = thingsToDraw.getFoods();
            g.scoreToDraw = thingsToDraw.getScoreString();
            g.livesToDraw = thingsToDraw.getLivesString();
            g.highScoreToDraw = thingsToDraw.getHighScoreString();
            g.drawGame();
        }
    }
    private final drawingHandler myDrawingHandler = new drawingHandler(this);
    public drawingHandler getMyDrawingHandler() { return myDrawingHandler; }

    private void drawGame() {
        //drawing sequence
        canvas = null;                                      //clear the canvas
        canvas = s.lockCanvas(null);                        //set the canvas to the canvas we can draw returned from lockCanvas()
        if (canvas != null){
            canvas.drawPaint(BACKGROUND_PAINT);

            playerToDraw.drawPlayer(canvas);                     //draw the player
            for (Food f:foodsToDraw) {f.drawFood(canvas);}       //draw the foods

            canvas.drawText(scoreToDraw, viewWidth2 /20, viewHeight2 /10, TEXT_PAINT); //draw the score text
            canvas.drawText(livesToDraw, viewWidth2 /3, viewHeight2 /10, TEXT_PAINT);
            canvas.drawText(highScoreToDraw, viewWidth2/2 + viewWidth2/15, viewHeight2/10, TEXT_PAINT);

            s.unlockCanvasAndPost(canvas);                       //finish drawing - stop editing the canvas and post it to the view
        }
    }

    // lifecycle methods

    public void pause() {
        gameLoop.setGameIsRunning(false);

        try {
            gameThread.join();
        }
        catch (InterruptedException e) {e.printStackTrace();}

        gameThread = null;
    }

    public void resume() {
        gameLoop.setGameIsRunning(true);
        gameThread = new Thread(gameLoop);
        gameThread.start();
    }

    public void restartGame() {
        gameLoop = null;
        gameThread = null;

        gameLoop = new GameLoop(this, viewWidth2, viewHeight2);
        gameLoop.setGameIsRunning(true);
        gameThread = new Thread(gameLoop);
        gameThread.start();
    }

    //override some interface methods because we implemented the SurfaceHolder.callback interface
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (canvas != null) {drawGame(); return;}
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
    @Override
    public void surfaceDestroyed(SurfaceHolder holder){}
}
