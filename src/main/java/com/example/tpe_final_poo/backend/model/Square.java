package com.example.tpe_final_poo.backend.model;

public class Square extends Rectangle{
    public Square(Point topLeft, Point bottomRight){
        //vamos a tomar la menor diferencia
        super(topLeft,bottomRight);
    }
}
