package com.andrewliang.glide;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

class Food
{
    public static final int GREEN_FOOD = 0;
    public static final int RED_FOOD = 1;
    public static final int LIFE_FOOD = 2;

    // set colors
    // pink
    private static final int pinkishRBG = (int)Long.parseLong("f99097", 16);
    private static final int pinkR = (pinkishRBG >> 16) & 0xFF;
    private static final int pinkG = (pinkishRBG >> 8) & 0xFF;
    private static final int pinkB = (pinkishRBG) & 0xFF;

    // grey
    private static final int greyishRBG = (int)Long.parseLong("636363", 16);
    private static final int greyR = (greyishRBG >> 16) & 0xFF;
    private static final int greyG = (greyishRBG >> 8) & 0xFF;
    private static final int greyB = (greyishRBG) & 0xFF;

    // yellow
    private static final int yellowishRBG = (int)Long.parseLong("d8be2b", 16);
    private static final int yellowR = (yellowishRBG >> 16) & 0xFF;
    private static final int yellowG = (yellowishRBG >> 8) & 0xFF;
    private static final int yellowB = (yellowishRBG) & 0xFF;

    // color array for determining color of a new food
    private static final int[] FOOD_COLORS = {Color.rgb(pinkR, pinkG, pinkB),
            Color.rgb(greyR, greyG, greyB),
            Color.rgb(yellowR, yellowG, yellowB)};

    private final int speed;      //the speed at which this food falls down the screen
    private final int xPos;       //x position of the center of the food
    private int yPos;       //y position of the center of the food
    private final int foodRadius;
    private final int foodType;   //type of food - atm, either GREEN_FOOD (0), RED_FOOD (1), or LIFE_FOOD (2)
    private int foodColor;  //one of the colors in FOOD_COLORS array based on foodType
    private Paint foodPaint = new Paint();

    Food(int speed, int xPos, int yPos, int radius, int type)
    {
        this.xPos = xPos;
        this.yPos = yPos;
        this.speed = speed;
        this.foodRadius = radius;
        this.foodType = type;

        this.foodColor = FOOD_COLORS[foodType];
        foodPaint.setColor(foodColor);
        foodPaint.setAntiAlias(true);
    }

    public int getXPos() {return xPos;}
    public int getYPos() {return yPos;}
    public int getFoodRadius() {return foodRadius;}
    public int getFoodType() {return foodType;}
    public int getSpeed() {return speed;}

    public void updateFoodPos() {yPos += speed;}

    public void drawFood(Canvas c) {
        c.drawCircle((float) xPos, (float) yPos, (float) foodRadius, foodPaint);
    }

}
