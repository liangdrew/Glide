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

public class GameLoop implements Runnable
{
    //view/graphics stuff
    private final GameView gameView;

    //gameplay variables
    private final static double PROBABILITY_OF_GREEN_FOOD_SPAWN = 0.0025;  // In decimal
    private final static double PROBABILITY_OF_RED_FOOD_SPAWN = 0.005;
    private final int viewWidth;
    private final int viewHeight;
    private int highScore;
    private final ArrayList<Food> foods = new ArrayList<Food>();

    //game state variables
    public boolean gameIsRunning = true;

    //player variables
    private Player player;
    private int playerLives = 3;
    private int playerScore = 0;

    //game loop timing variables
    private final static int MAX_FPS = 50;
    private final static int MAX_FRAME_SKIPS = 5;
    private final static int FRAME_PERIOD = 1000/MAX_FPS;   // the max time per frame in ms

    public GameLoop(GameView g, int w, int h)
    {
        this.gameView = g;
        this.viewWidth = w;  // Since game is landscape
        this.viewHeight = h;
        this.player = new Player(viewWidth, viewHeight);  // Fix proportions
    }

    public Player getPlayer() {return this.player;}
    public int getPlayerScore() {return this.playerScore;}
    public int getPlayerLives(){return this.playerLives;}
    public ArrayList<int[]> getFoodData(){
        ArrayList<int[]> ret = new ArrayList<int[]>();
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

    public void setGameIsRunning (boolean r) {gameIsRunning = r;}   //sets the state of the game to running or not

    @Override
    public void run()
    {
        synchronized (gameView)
        {
            long beginTime;     //the time at the start of the loop iteration
            long timeDiff;      //the time it took for the update/render cycle to execute
            int sleepTime;      //ms to sleep (<0 if we're behind)
            int framesSkipped;  //number of frame renders being skipped when we're behind

            while (gameIsRunning) {
                try
                {
                    beginTime = System.currentTimeMillis();
                    framesSkipped = 0;                      //reset the frames skipped

                    //Log.d("GameLoop", "foods width" + gameView.getMeasuredWidth());
                    updateState();


                    timeDiff = System.currentTimeMillis() - beginTime;
                    sleepTime = (int) (FRAME_PERIOD - timeDiff);

                    if (sleepTime > 0)  //means there is left over time in the frame period
                    {
                        try
                        {
                            //sleep the thread for sleepTime ms
                            Thread.sleep(sleepTime);
                        }
                        catch (InterruptedException e)
                        {
                            Log.d("GameLoop", "1st catch " + e.getMessage());
                        }
                    }

                    while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS)
                    {
                        //UPDATE GAME STATE WITHOUT RENDERING
                        updateState();
                        generateFood();
                        checkCollision(foods, player);
                        player.updatePlayer(viewWidth, viewHeight);
                        sleepTime += FRAME_PERIOD;      //sleepTime becomes the time difference between the next frame and the last render
                        framesSkipped++;                //add another skipped frame
                    }
                }
                catch (Exception e)
                {
                    Log.d("GameLoop", "2nd catch " + e.getMessage());
                    //e.printStackTrace();
                }
            }
        }
    }

    private void updateState()
    {
        player.updatePlayer(viewWidth, viewHeight);
        checkCollision(foods, player);

        //update game state (just updating the player for now)
        generateFood();
        for (Food f:foods)
        {
            f.updateFoodPos();
            if (f.getYPos() > viewHeight + f.getFoodRadius())
            {
                foods.remove(f);
            }
        }

        //package the updated foods and player into a box to send as a message to the handler in gameview
        Box<ArrayList<Food>, Player, String, String, String> messageBox = new Box<ArrayList<Food>, Player, String, String, String>();
        messageBox.setFoods(foods);
        messageBox.setPlayer(player);
        String scoreString = "Score: ";
        messageBox.setScoreString(scoreString + playerScore);
        String livesString = "Lives: ";
        messageBox.setLivesString(livesString + playerLives);
        String highScoreString = "High Score: ";
        messageBox.setHighScoreString(highScoreString + gameView.getGameActivity().getPrefs().getInt("highScore", highScore));

        //to render the game state, package current game info (just the player for now) and send it to the handler in GameView which draws the player
        Message gameState = Message.obtain();           //obtain a message from a global pool of recycled messages to avoid creating a new one each time
        gameState.obj = messageBox;                         //set the message as the game loop's player object
        gameView.drawingHandler.sendMessage(gameState); //send the message containing the player to the GameView handler

        checkDeath();
    }

    private void generateFood()
    {
        double r = Math.random();                   //get a random number r between 0 and 1
        Random rand = new Random();
        if (r <= PROBABILITY_OF_GREEN_FOOD_SPAWN)
        {
            int speed = rand.nextInt(viewWidth/200) + 1;    //random speed between 1 and 5 pixels per update
            int xPosition = rand.nextInt((int)(viewWidth*59/60 - viewWidth/60 + 1)) + viewWidth/60;     //random x position between 1/60th width and 59/60 width of view
            foods.add(new Food(speed, xPosition, 0, viewWidth / 60, 0));     //add a new green food to foods
        }
        else if (r > PROBABILITY_OF_GREEN_FOOD_SPAWN && r <= (PROBABILITY_OF_GREEN_FOOD_SPAWN + PROBABILITY_OF_RED_FOOD_SPAWN))
        {
            int speed = rand.nextInt(viewWidth/200) + 1;    //random speed between 1 and 5 pixels per update
            int xPosition = rand.nextInt((int)(viewWidth*59/60 - viewWidth/60 + 1)) + viewWidth/60;     //random x position between 1/60th width and 59/60 width of view
            foods.add(new Food(speed, xPosition, 0, viewWidth / 60, 1));       //ad a new red food to foods
        }
    }

    private void checkCollision(ArrayList<Food> foods, Player player)
    {
        double collisionParameter;
        double[] distancesSquared;
        distancesSquared = new double[2];
        Vector2 FoodCenter;
        Vector2 VP;
        Vector2 VW;
        Vector2 ClosestPointOnSegment;

        Vector2 EndPoint1 = new Vector2(player.getStartX(), player.getStartY());    //call this vector V
        Vector2 EndPoint2 = new Vector2(player.getStopX(), player.getStopY());      //call this vector W

        for (Food f : foods)
        {
            FoodCenter = new Vector2(f.getXPos(), f.getYPos());
            VP = FoodCenter.subtract(EndPoint1);
            VW = EndPoint2.subtract(EndPoint1);

            collisionParameter = VP.getDotProductWith(VW)/VW.getMagSquared();

            if (collisionParameter <= 1 && collisionParameter >= 0)
            {
                ClosestPointOnSegment = EndPoint1.add(VW.ScalarMult(collisionParameter));       //get closest point
                distancesSquared[0] = FoodCenter.getDistanceSquaredTo(ClosestPointOnSegment);   //get distance from food to closest point
                distancesSquared[1] = 0;

                if (distancesSquared[0] <= (Math.pow(f.getFoodRadius(), 2)))
                {
                    foods.remove(f);
                    updateScore(f);
                }
            }
            else
            {
                distancesSquared[0] = FoodCenter.getDistanceSquaredTo(EndPoint1);
                distancesSquared[1] = FoodCenter.getDistanceSquaredTo(EndPoint2);

                if (distancesSquared[0] <= Math.pow(f.getFoodRadius(), 2) || distancesSquared[1] <= Math.pow(f.getFoodRadius(), 2))
                {
                    foods.remove(f);
                    updateScore(f);
                }
            }
        }
    }

    private void updateScore(Food f)
    {
        if (f.getFoodType() == 0)  // Green
        {
            playerScore += f.getSpeed()*4;

        }
        else if (f.getFoodType() == 1)  // Red
        {
            if (playerScore != 0){playerScore -= Math.abs(f.getSpeed() - 5);}
            if (playerScore < 0){playerScore = 0;}  // Prevents negative score
            playerLives -= 1;
//            checkDeath();
        }
    }

    private void checkDeath()
    {
        if (playerLives == 0)
        {
//            highScore = playerScore;
//            setHighScore();
            setGameIsRunning(false);
            Message deathMessage = Message.obtain();
            deathMessage.arg1 = 1;      //indicates death
            gameView.getGameActivity().gameLoopMessageHandler.sendMessage(deathMessage);
        }

    }
}