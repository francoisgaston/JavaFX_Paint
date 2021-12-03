package com.example.tpe_final_poo.backend.model;

public class Line extends Figure{
    public Line(Point topLeft, Point bottomRight){
        super(topLeft,bottomRight);
    }
    @Override
    public String toString() {
        return String.format("Linea [ %s , %s ]", getTopLeft(), getBottomRight());
    }
    @Override
    public boolean pointBelongs(Point point){
        return false;
    }
}
