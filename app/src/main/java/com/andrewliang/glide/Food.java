package com.andrewliang.glide;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

class Food
{
    private final int GREEN_FOOD = 0;                             //for use when creating a new food
    private final int RED_FOOD = 1;                               //for use when creating a new food

    private final int speed;      //the speed at which this food falls down the screen
    private final int xPos;       //x position of the center of the food
    private int yPos;       //y position of the center of the food
    private final int foodRadius;
    private final int foodType;   //type of food - atm, either GREEN_FOOD ( = 0) OR RED_FOOD (= 1)
    private final int foodColor;  //red or green, depending on foodType

    Food(int speed, int xPos, int radius, int type)
    {
        this.xPos = xPos;
        this.yPos = 0;
        this.speed = speed;
        this.foodRadius = radius;
        this.foodType = type;
        int[] FOOD_COLORS = {Color.GREEN, Color.RED};
        this.foodColor = FOOD_COLORS[type];
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
