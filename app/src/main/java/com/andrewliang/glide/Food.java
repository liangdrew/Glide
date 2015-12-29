package com.andrewliang.glide;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ListIterator;

class Food
{
    private int GREEN_FOOD = 0;                             //for use when creating a new food
    private final int RED_FOOD = 1;                               //for use when creating a new food

    private final int speed;      //the speed at which this food falls down the screen
    private final int xPos;       //x position of the center of the food
    private int yPos;       //y position of the center of the food
    private final int foodRadius;
    private final int foodType;   //type of food - atm, either GREEN_FOOD ( = 0) OR RED_FOOD (= 1)
    private int foodColor;  //red or green, depending on foodType

    Food(int speed, int xPos, int yPos, int radius, int type)
    {
        this.xPos = xPos;
        this.yPos = yPos;
        this.speed = speed;
        this.foodRadius = radius;
        this.foodType = type;
        setColors();
    }

    private void setColors(){
        Color pinkish = new Color();
        int pinkishRBG = (int)Long.parseLong("FF4079", 16);
        int pinkR = (pinkishRBG >> 16) & 0xFF;
        int pinkG = (pinkishRBG >> 8) & 0xFF;
        int pinkB = (pinkishRBG) & 0xFF;

        Color greyish = new Color();
        int greyishRBG = (int)Long.parseLong("636363", 16);
        int greyR = (greyishRBG >> 16) & 0xFF;
        int greyG = (greyishRBG >> 8) & 0xFF;
        int greyB = (greyishRBG) & 0xFF;

        int[] FOOD_COLORS = {Color.rgb(pinkR, pinkG, pinkB), Color.rgb(greyR, greyG, greyB)};
        this.foodColor = FOOD_COLORS[foodType];
    }

    public int getXPos() {return xPos;}
    public int getYPos() {return yPos;}
    public int getFoodRadius() {return foodRadius;}
    public int getFoodType() {return foodType;}
    public int getSpeed() {return speed;}

    public void updateFoodPos() {yPos += speed;}

    public void drawFood(Canvas c)
    {
        Paint paint = new Paint();
        paint.setColor(this.foodColor);
        paint.setAntiAlias(true);
        c.drawCircle((float) xPos, (float) yPos, (float) foodRadius, paint);
    }

}
