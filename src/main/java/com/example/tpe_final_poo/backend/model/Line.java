package com.example.tpe_final_poo.backend.model;

public class Line extends Figure {
    private Point topLeft, bottomRight;

    public Line(Point topLeft, Point bottomRight){
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
    }
    @Override
    protected Point[] getPoints(){
        return new Point[]{topLeft,bottomRight};
    }
    public Point getTopLeft() {
        return topLeft;
    }

    public Point getBottomRight() {
        return bottomRight;
    }
    public String toString() {
        return String.format("Linea [ %s , %s ]", getTopLeft(), getBottomRight());
    }
    @Override
    public boolean pointBelongs(Point point){
        if(point.getX() > bottomRight.getX() || point.getX() < topLeft.getX() || point.getY() > bottomRight.getY() || point.getY() < topLeft.getY())
            return false;
        double m = (topLeft.getY() - bottomRight.getY()) / (topLeft.getX() - bottomRight.getX());
        double b = topLeft.getY() - topLeft.getX() * m;
        return point.getY() == point.getX() * m + b;
    }
}
