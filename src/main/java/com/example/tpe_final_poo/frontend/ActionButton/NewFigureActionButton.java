package com.example.tpe_final_poo.frontend.ActionButton;

import com.example.tpe_final_poo.backend.model.*;
import com.example.tpe_final_poo.frontend.PaintPane;
import com.example.tpe_final_poo.frontend.frontEndModel.FrontFigure;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;

import java.util.function.*;

public class NewFigureActionButton extends ToggleButton{
    private final BackFigureFactory backFigureFactory; //como crea la figura del back
    private final FrontFigureFactory frontFigureFactory;//crea la figura del front
    public BiPredicate<Point,Point> canCreate;

    public NewFigureActionButton(String text, BackFigureFactory backFigureFactory, FrontFigureFactory frontFigureFactory, BiPredicate<Point,Point> canCreate) {
        super(text);
        this.backFigureFactory = backFigureFactory;
        this.frontFigureFactory = frontFigureFactory;
        this.canCreate = canCreate;
    }
    public void createShape (Point startPoint,Point endPoint, Color fillColor, Color lineColor, double lineWidth, PaintPane paintPane){
        if(isSelected() && canCreate.test(startPoint,endPoint)){
            Figure newFigure = backFigureFactory.get(startPoint,endPoint);
            FrontFigure newFrontFigure = frontFigureFactory.get(fillColor,lineColor,lineWidth);
            paintPane.addBackFigure(newFigure);
            paintPane.addFrontFigure(newFigure,newFrontFigure);
        }
    }
}
