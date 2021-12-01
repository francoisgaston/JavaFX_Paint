package com.example.tpe_final_poo.backend.model;

public class Line extends Figure {
    //Puede ser que tenga sentido que extienda a Rectangle y cambiamos un par de cosas?
    //Una linea no es un rectangulo, entonces puede ser que extender no sea una buena opcion
    //pero podemos hacer composicion, porque muchos metodos son iguales
//    private Point topLeft, bottomRight;
    private Rectangle rectangle;
    public Line(Point topLeft, Point bottomRight){
        this.rectangle = new Rectangle(topLeft,bottomRight);
    }
    @Override
    protected Point[] getPoints(){
        return rectangle.getPoints();
    }
    public Point getTopLeft() {
        return rectangle.getTopLeft();
    }

    public Point getBottomRight() {
        return rectangle.getBottomRight();
    }
    public String toString() {
        return String.format("Linea [ %s , %s ]", getTopLeft(), getBottomRight());
    }
    @Override
    public boolean pointBelongs(Point point){
//        if(point.getX() > rectangle.getBottomRight().getX() || point.getX() < rectangle.getTopLeft().getX() || point.getY() > rectangle.getBottomRight().getY() || point.getY() < rectangle.getTopLeft().getY())
//            return false;
//        double m = (rectangle.getTopLeft().getY() - rectangle.getBottomRight().getY()) / (rectangle.getTopLeft().getX() - rectangle.getBottomRight().getX());
//        double b = rectangle.getTopLeft().getY() - rectangle.getTopLeft().getX() * m;
//        return point.getY() == point.getX() * m + b;
        return false;
    }

    @Override
    public boolean isInRectangle(Rectangle rectangle) {
        return this.rectangle.isInRectangle(rectangle);
    }
}
