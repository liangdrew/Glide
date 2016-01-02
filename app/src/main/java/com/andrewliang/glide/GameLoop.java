package com.andrewliang.glide;

import android.os.Message;
import android.util.Log;
import java.lang.Exception;
import java.lang.InterruptedException;
import java.lang.Math;
import java.lang.Runnable;
import java.lang.String;
import java.lang.System;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.Random;

public class GameLoop implements Runnable {
    //view/graphics stuff
    private final GameView gameView;

    //gameplay variables
    private final static double PROBABILITY_OF_GREEN_FOOD_SPAWN = 0.005;  // In decimal
    private final static double PROBABILITY_OF_RED_FOOD_SPAWN = 0.01;
    private final int viewWidth;
    private final int viewHeight;
    private final ArrayList<Food> foods = new ArrayList<Food>();

    //player variables
    private Player player;
    private int playerLives = 3;
    private int playerScore = 0;

    //game loop timing variables
    public boolean gameIsRunning = true;
    private final static int MAX_FPS = 50;
    private final static int MAX_FRAME_SKIPS = 5;
    private final static int FRAME_PERIOD = 1000/MAX_FPS;   // the max time per frame in ms

    public GameLoop(GameView g, int w, int h) {
        this.gameView = g;
        this.viewWidth = w;  // Since game is landscape
        this.viewHeight = h;
        this.player = new Player(viewWidth, viewHeight);  // Fix proportions
    }

    // lots of setters and getters, because we have to recreate the game loop when the screen is locked/unlocked
    public void setGameIsRunning (boolean r) {gameIsRunning = r;}   //sets the state of the game to running or not
    public Player getPlayer() {return this.player;}
    public int getPlayerScore() {return this.playerScore;}
    public int getPlayerLives() {return this.playerLives;}
    public ArrayList<int[]> getFoodData() {
        ArrayList<int[]> ret = new ArrayList<>();
        int[] xPos = new int[foods.size()];
        int[] yPos = new int[foods.size()];
        int[] speeds = new int[foods.size()];
        int[] types = new int[foods.size()];
        int i = 0;
        for (Food f: foods){
            xPos[i] = f.getXPos();
            yPos[i] = f.getYPos();
            speeds[i] = f.getSpeed();
            types[i] = f.getFoodType();
            i++;
        }
        ret.add(xPos);
        ret.add(yPos);
        ret.add(speeds);
        ret.add(types);
        return ret;
    }
    public void setPlayer(Player p){this.player = p;}
    public void setPlayerScore(int s){this.playerScore = s;}
    public void setPlayerLives(int l){this.playerLives = l;}
    public void setFoods(int[] xPos, int[] yPos, int[] speeds, int[]
            types){
        for (int i = 0; i < xPos.length; i++){
            foods.add(new Food(speeds[i], xPos[i], yPos[i], viewWidth /
                    60, types[i]));
        }
    }

    @Override
    public void run() {
        synchronized (gameView) {
            long beginTime;     //the time at the start of the loop iteration
            long timeDiff;      //the time it took for the update/render cycle to execute
            int sleepTime;      //ms to sleep (<0 if we're behind)
            int framesSkipped;  //number of frame renders being skipped when we're behind

            while (gameIsRunning) {
                try {
                    // get iteration start time and reset skipped frames
                    beginTime = System.currentTimeMillis();
                    framesSkipped = 0;

                    // update state of the game - food position, player position, and score, then draw
                    updateState();

                    // get the update time in ms
                    timeDiff = System.currentTimeMillis() - beginTime;
                    sleepTime = (int) (FRAME_PERIOD - timeDiff);

                    // if there is left over time in the frame period then sleep
                    if (sleepTime > 0){
                        try {
                            //sleep the thread for sleepTime ms
                            Thread.sleep(sleepTime);
                        }
                        catch (InterruptedException e) {
                            //Log.d("GameLoop", e.getMessage());
                        }
                    }

                    // if the update time took longer than a frame period, then skip frames, i.e. update without rendering
                    while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
                        updateState();
                        generateFood();
                        checkCollision();
                        player.updatePlayer(viewWidth, viewHeight);
                        sleepTime += FRAME_PERIOD;//sleepTime becomes the time difference between the next frame and the last render
                        framesSkipped++;
                    }
                }
                catch (Exception e) {
                    //Log.d("GameLoop", e.getMessage());
                }
            }
        }
    }

    private void updateState() {
        player.updatePlayer(viewWidth, viewHeight);
        checkCollision();

        //update game state (just updating the player for now)
        generateFood();

        // remove foods that are past the boundary
        ArrayList<Food> foodsToRemove = new ArrayList<Food>();
        for (Food f:foods) {
            f.updateFoodPos();
            if (f.getYPos() > viewHeight + f.getFoodRadius()) {
                foodsToRemove.add(f);
            }
        }
        for (Food f:foodsToRemove){
            foods.remove(f);
        }

        //package the updated foods and player into a box to send as a message to the handler in gameview
        Box<ArrayList<Food>, Player, String, String, String> messageBox = new Box<ArrayList<Food>, Player, String, String, String>();
        messageBox.setFoods(foods);
        messageBox.setPlayer(player);
        messageBox.setScoreString("Score: " + Integer.toString(playerScore));
        messageBox.setLivesString("Lives: " + Integer.toString(playerLives));
        messageBox.setHighScoreString("High Score: " + gameView.getGameActivity().getPrefs().getInt("highScore", 0)); // Set default to 0 in case there is no high score stored yet

        //to render the game state, package current game data and send it to the handler in GameView
        Message gameState = Message.obtain();
        gameState.obj = messageBox;
        gameView.getMyDrawingHandler().sendMessage(gameState);

        checkDeath();
    }

    private void generateFood() {
        double r = Math.random();                   //get a random number r between 0 and 1
        Random rand = new Random();
        if (r <= PROBABILITY_OF_GREEN_FOOD_SPAWN) {
            int speed = rand.nextInt(viewWidth/200) + 1;    //random speed between 1 and 5 pixels per update
            int xPosition = rand.nextInt((int)(viewWidth*59/60 - viewWidth/60 + 1)) + viewWidth/60;     //random x position between 1/60th width and 59/60 width of view
            foods.add(new Food(speed, xPosition, 0, viewWidth / 60, 0));     //add a new green food to foods
        }
        else if (r > PROBABILITY_OF_GREEN_FOOD_SPAWN && r <= (PROBABILITY_OF_GREEN_FOOD_SPAWN + PROBABILITY_OF_RED_FOOD_SPAWN)) {
            int speed = rand.nextInt(viewWidth/200) + 1;    //random speed between 1 and 5 pixels per update
            int xPosition = rand.nextInt((int)(viewWidth*59/60 - viewWidth/60 + 1)) + viewWidth/60;     //random x position between 1/60th width and 59/60 width of view
            foods.add(new Food(speed, xPosition, 0, viewWidth / 60, 1));       //ad a new red food to foods
        }
    }

    private void checkCollision() {
        double collisionParameter;
        double[] distancesSquared = new double[2];
        Vector2 FoodCenter;
        Vector2 VP;
        Vector2 VW;
        Vector2 ClosestPointOnSegment;
        ArrayList<Food> foodsToRemove = new ArrayList<Food>();

        Vector2 EndPoint1 = new Vector2(player.getStartX(), player.getStartY());    //call this vector V
        Vector2 EndPoint2 = new Vector2(player.getStopX(), player.getStopY());      //call this vector W

        for (Food f : foods) {
            FoodCenter = new Vector2(f.getXPos(), f.getYPos());
            VP = FoodCenter.subtract(EndPoint1);
            VW = EndPoint2.subtract(EndPoint1);

            collisionParameter = VP.getDotProductWith(VW)/VW.getMagSquared();

            if (collisionParameter <= 1 && collisionParameter >= 0) {
                ClosestPointOnSegment = EndPoint1.add(VW.ScalarMult(collisionParameter));       //get closest point
                distancesSquared[0] = FoodCenter.getDistanceSquaredTo(ClosestPointOnSegment);   //get distance from food to closest point
                distancesSquared[1] = 0;

                if (distancesSquared[0] <= (Math.pow(f.getFoodRadius(), 2))) {
                    foodsToRemove.add(f);
                    updateScore(f);
                }
            }
            else {
                distancesSquared[0] = FoodCenter.getDistanceSquaredTo(EndPoint1);
                distancesSquared[1] = FoodCenter.getDistanceSquaredTo(EndPoint2);

                if (distancesSquared[0] <= Math.pow(f.getFoodRadius(), 2) || distancesSquared[1] <= Math.pow(f.getFoodRadius(), 2)) {
                    foodsToRemove.add(f);
                    updateScore(f);
                }
            }
        }

        for (Food f: foodsToRemove){
            foods.remove(f);
        }
    }

    private void updateScore(Food f) {
        if (f.getFoodType() == 0){  // Green
            playerScore += f.getSpeed()*4;
        }
        else if (f.getFoodType() == 1){  // Red
            if (playerScore != 0)playerScore -= Math.abs(f.getSpeed() - 5);
            if (playerScore < 0) playerScore = 0;  // Prevents negative score
            playerLives -= 1;
        }
    }

    private void checkDeath() {
        if (playerLives == 0) {
            setGameIsRunning(false);
            Message deathMessage = Message.obtain();
            deathMessage.arg1 = 1;      //indicates death
            gameView.getGameActivity().getMyGameLoopHandler().sendMessage(deathMessage);
        }
    }
}