package com.example.tpe_final_poo.backend.model;

public abstract class Figure {//propiedades generales de todas
    private final Point topLeft;
    private final Point bottomRight;
    private static final int INITIAL_VALUE = 0;
    private static int currentId = INITIAL_VALUE;
    private final int id;

    public Figure(Point topLeft, Point bottomRight){
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
        this.id = getAndIncrementId();
    }
    protected Point[] getPoints(){
        return new Point[]{topLeft,bottomRight};
    }
    public void moveX(double deltaX){
        for (Point point : getPoints()){
            point.moveX(deltaX);
        }
    }
    public Point getTopLeft(){ return topLeft;}
    public Point getBottomRight(){ return bottomRight;}
    public double getWidth(){
        return Math.abs(bottomRight.getX() - topLeft.getX());
    }
    public double getHeight(){
        return Math.abs(topLeft.getY() - bottomRight.getY());
    }
    public void moveY(double deltaY){
        for(Point point : getPoints()){
            point.moveY(deltaY);
        }
    }
    public abstract boolean pointBelongs(Point point);
    public boolean isInRectangle(Rectangle rectangle){
        return rectangle.pointBelongs(topLeft) && rectangle.pointBelongs(bottomRight);
    }
    public int getAndIncrementId() {
        return currentId++;
    }
    public int getId(){
        return id;
    }
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(!(o instanceof Figure figure)){
            return false;
        }
        return id == figure.getId();
    }
}


