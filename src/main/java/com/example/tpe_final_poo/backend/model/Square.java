package com.example.tpe_final_poo.backend.model;

public class Square extends Rectangle{
    public Square(Point topLeft, Point bottomRight){
        super(topLeft, new Point(topLeft.getX() + Math.abs(bottomRight.getX() - topLeft.getX()), topLeft.getY() + Math.abs(bottomRight.getX() - topLeft.getX())));
    }
    @Override
    public String toString() {
        return String.format("Cuadrado [ %s , %s ]", getTopLeft(), getBottomRight());
    }
}
