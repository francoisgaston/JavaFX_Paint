package com.example.tpe_final_poo.backend.model;

public class Ellipse extends Figure{
    private final Point centerPoint;
    public Ellipse(Point topLeft, Point bottomRight){
        super(topLeft, bottomRight);
        this.centerPoint = new Point(topLeft.getX() + getxRadius(), bottomRight.getY() - getyRadius());
    }
    @Override
    protected Point[] getPoints(){
        return new Point[]{getTopLeft(), getBottomRight(),centerPoint};
    }
    public Point getCenterPoint() {
        return centerPoint;
    }
    public double getxRadius(){
        return getWidth()/2;
    }
    public double getyRadius(){
        return getHeight()/2;
    }
    @Override
    public String toString(){
        return String.format("Elipse [Centro: %s, Semieje mayor: %.2f, Semieje menor: %.2f]", centerPoint,getWidth(),getHeight());
    }
    @Override
    public boolean pointBelongs(Point point){
        double elem1 = Math.pow(point.getX() - centerPoint.getX(), 2) / ( getxRadius() * getxRadius() );
        double elem2 = Math.pow(point.getY() - centerPoint.getY(), 2) / ( getyRadius() * getyRadius() );
        return elem1 + elem2 <= 1;
    }

}
