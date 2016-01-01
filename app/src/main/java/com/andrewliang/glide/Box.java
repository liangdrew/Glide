package com.andrewliang.glide;

class Box<T1, T2, T3, T4> {
    private T1 foods;
    private T2 player;
    private T3 scoreString;
    private T4 livesString;

    public void setFoods(T1 f) {foods = f;}
    public void setPlayer(T2 p) {player = p;}
    public void setScoreString(T3 s) {scoreString = s;}
    public void setLivesString(T4 l) {livesString = l;}
    public T1 getFoods() {return foods;}
    public T2 getPlayer() {return player;}
    public T3 getScoreString() {return scoreString;}
    public T4 getLivesString() {return livesString;}
}
