package com.example.tpe_final_poo.backend.model;

public class Point {

    private double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public void moveX(double deltaX){
        x+=deltaX;
    }
    public void moveY(double deltaY){
        y+=deltaY;
    }
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return String.format("{%.2f , %.2f}", x, y);
    }

}
