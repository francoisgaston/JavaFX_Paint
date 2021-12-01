package com.example.tpe_final_poo.backend.model;

public abstract class Figure {//propiedades generales de todas
    //id de la figura
    protected abstract Point[] getPoints();
    public void moveX(double deltaX){
        for (Point point : getPoints()){
            point.moveX(deltaX);
        }
    }
    public void moveY(double deltaY){
        for(Point point : getPoints()){
            point.moveY(deltaY);
        }
    }
    public abstract boolean pointBelongs(Point point);
    public abstract boolean isInRectangle(Rectangle rectangle);
}
//Ej 3 TP4
