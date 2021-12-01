package com.example.tpe_final_poo.backend.model;

public class Ellipse extends Figure{
    //private Point topLeft, bottomRight;
    private Point centerPoint;
    //private double xRadius, yRadius;
    private Rectangle rectangle;
    public Ellipse(Point topLeft, Point bottomRight){
        this.rectangle = new Rectangle(topLeft,bottomRight);
//        this.topLeft = topLeft;
//        this.bottomRight = bottomRight;
//        this.xRadius = Math.abs(bottomRight.getX() - topLeft.getX())/2;
//        this.yRadius = Math.abs(topLeft.getY()-bottomRight.getY())/2;
        this.centerPoint = new Point(topLeft.getX() + getxRadius(), bottomRight.getY() - getyRadius());
        //System.out.printf("center: %s, x:%s y:%s", centerPoint, xRadius, yRadius);
    }
    // Este es el que utiliza el círculo
    protected Ellipse(Point centerPoint, double radius){
        this.centerPoint = centerPoint;
//        this.xRadius = radius;
//        this.yRadius = radius;
        Point topLeft = new Point(centerPoint.getX() - radius, centerPoint.getY()-radius);
        Point bottomRight = new Point(centerPoint.getX() + radius, centerPoint.getY() + radius);
        this.rectangle = new Rectangle(topLeft,bottomRight);
    }
    @Override
    protected Point[] getPoints(){
        return new Point[]{rectangle.getTopLeft(),rectangle.getBottomRight(),centerPoint};
    }
    public Point getCenterPoint() {
        return centerPoint;
    }
    public double getxRadius(){
        return rectangle.getWidth()/2;
    }
    public double getyRadius(){
        return  rectangle.getHeight()/2;
    }
    public Point getTopLeft() {return rectangle.getTopLeft();}
    public Point getBottomRight() {return rectangle.getBottomRight();}

    @Override
    public String toString(){
        return String.format("Elipse [Centro: %s, Semieje mayor: %.2f, Semieje menor: %.2f]", getCenterPoint(),rectangle.getWidth(),rectangle.getHeight());
    }
    @Override
    public boolean pointBelongs(Point point){
        double elem1 = Math.pow(point.getX()-centerPoint.getX(),2)/(getyRadius()*getxRadius());
        double elem2 = Math.pow(point.getY()-centerPoint.getY(),2)/(getyRadius()*getyRadius());
        return elem1 + elem2 <= 1;
    }
    @Override
    public boolean isInRectangle(Rectangle rectangle){
        return new Rectangle(rectangle.getTopLeft(),rectangle.getBottomRight()).isInRectangle(rectangle);
    }

}