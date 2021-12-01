package com.example.tpe_final_poo.backend.model;

public class Rectangle extends Figure {

    private final Point topLeft, bottomRight;

    public Rectangle(Point topLeft, Point bottomRight) {
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
    public double getWidth(){
        return Math.abs(topLeft.getX() - bottomRight.getX());
    }
    public double getHeight(){
        return Math.abs(topLeft.getY() - bottomRight.getY());
    }
    @Override
    public String toString() {
        return String.format("Rectángulo [ %s , %s ]", topLeft, bottomRight);
    }
    @Override
    public boolean pointBelongs(Point point){
        return point.getX() > getTopLeft().getX() && point.getX() < getBottomRight().getX() &&
                point.getY() > getTopLeft().getY() && point.getY() < getBottomRight().getY();
    }
    @Override
    public boolean isInRectangle(Rectangle rectangle){
        return rectangle.pointBelongs(topLeft) && rectangle.pointBelongs(bottomRight);
    }
}
