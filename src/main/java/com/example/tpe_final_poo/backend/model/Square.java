package com.example.tpe_final_poo.backend.model;

public class Square extends Rectangle{
    public Square(Point topLeft, Point bottomRight){
        //Vamos a tomar la menor diferencia
        super(topLeft, new Point(
                topLeft.getX() + Math.min(Math.abs(topLeft.getX() - bottomRight.getX()), Math.abs(topLeft.getY() - bottomRight.getY())),
                topLeft.getY() + Math.min(Math.abs(topLeft.getX() - bottomRight.getX()), Math.abs(topLeft.getY() - bottomRight.getY()))));
    }
    @Override
    public String toString() {
        return String.format("Cuadrado [ %s , %s ]", getTopLeft(), getBottomRight());
    }
}
