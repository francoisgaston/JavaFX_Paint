package com.example.tpe_final_poo.backend.model;

public class Ellipse extends Figure{
    private Point topLeft, bottomRight;
    private Point centerPoint;
    private double xRadius, yRadius;
    public Ellipse(Point topLeft, Point bottomRight){
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
        this.xRadius = (bottomRight.getX() - topLeft.getX())/2;
        this.yRadius = (topLeft.getY()-bottomRight.getY())/2;
        this.centerPoint = new Point(topLeft.getX() + xRadius, bottomRight.getY() + yRadius);
    }
    protected Ellipse(Point centerPoint, double radius){
        this.centerPoint = centerPoint;
        this.xRadius = radius;
        this.yRadius = radius;
    }
    @Override
    protected Point[] getPoints(){
        return new Point[]{topLeft,bottomRight};
    }
    public Point getCenterPoint() {
        return centerPoint;
    }
    public double getxRadius(){
        return xRadius;
    }
    public double getyRadius(){
        return  yRadius;
    }
    @Override
    public String toString(){
        return String.format("Elipse [Centro: %s, Semieje mayor: %.2f, Semieje menor: %.2f]", getCenterPoint(),xRadius,yRadius);
    }
    @Override
    public boolean pointBelongs(Point point){
        double elem1 = Math.pow(point.getX()-centerPoint.getX(),2)/(xRadius*xRadius);
        double elem2 = Math.pow(point.getY()-centerPoint.getY(),2)/(yRadius*yRadius);
        return elem1 + elem2<=1;
    }
}
