package com.andrewliang.glide;

public class Vector2
{
    //the x and y values of the vector are public
    private final double xVal;
    private final double yVal;

    //constructor takes x and y position input and sets them to xVal and yVal
    public Vector2(double x, double y)
    {
        xVal = x;
        yVal = y;
    }

    //get methods for x and y vals
    private double getXVal(){return xVal;}
    private double getYVal(){return yVal;}

    //add current vector with other
    public Vector2 add(Vector2 other)
    {
        return new Vector2(xVal + other.getXVal(), yVal + other.getYVal());
    }

    //subtracts current vector with the vector being passed
    public Vector2 subtract(Vector2 other)
    {
        return new Vector2(xVal - other.getXVal(), yVal - other.getYVal());
    }

    //magnitude squared method
    public double getMagSquared()
    {
        return xVal * xVal + yVal * yVal;
    }

    //distance to another vector method
    public double getDistanceSquaredTo(Vector2 other)
    {
        return Math.pow((xVal - other.getXVal()), 2) + Math.pow((yVal - other.getYVal()), 2);
    }

    //scalar multiplication method
    public Vector2 ScalarMult(double a)
    {
        return new Vector2(a* xVal, a* yVal);
    }

    //dot product with another vector method
    public double getDotProductWith(Vector2 other)
    {
        return xVal *other.getXVal() + yVal *other.getYVal();
    }
}
