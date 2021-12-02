package com.example.tpe_final_poo.backend.model;

public class Circle extends Ellipse {

//    public Circle(Point centerPoint, double radius) {
//        super(centerPoint,radius);
//    }
    public Circle(Point centerPoint, Point bottomRight){//TODO: arreglar esto
        super(new Point(centerPoint.getX()-Math.abs(bottomRight.getX() - centerPoint.getX()),centerPoint.getY() - Math.abs(bottomRight.getX() - centerPoint.getX())),new Point(centerPoint.getX()+Math.abs(bottomRight.getX() - centerPoint.getX()),centerPoint.getY()+ Math.abs(bottomRight.getX() - centerPoint.getX())));
    }
    @Override
    public String toString() {
        return String.format("Círculo [Centro: %s, Radio: %.2f]", getCenterPoint(), getRadius());
    }
    public double getRadius() {
        return getxRadius();
    }
}
