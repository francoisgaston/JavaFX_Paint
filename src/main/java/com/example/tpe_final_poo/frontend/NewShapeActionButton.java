package com.example.tpe_final_poo.frontend;

import com.example.tpe_final_poo.backend.model.*;
import com.example.tpe_final_poo.frontend.frontEndModel.FrontEllipse;
import com.example.tpe_final_poo.frontend.frontEndModel.FrontFigure;
import com.example.tpe_final_poo.frontend.frontEndModel.FrontLine;
import com.example.tpe_final_poo.frontend.frontEndModel.FrontRectangle;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;

import java.util.function.*;

public class NewShapeActionButton {
    private ToggleButton button; //Boton de UI
    BackFigureFactory backFigureFactory; //como crea la figura del back
    FrontFigureFactory frontFigureFactory;//crea la figura del front
    public BiPredicate<Point,Point> canCreate;

    public NewShapeActionButton(ToggleButton button, BackFigureFactory backFigureFactory, FrontFigureFactory frontFigureFactory,BiPredicate<Point,Point> canCreate) {
        this.button = button;
        this.backFigureFactory = backFigureFactory;
        this.frontFigureFactory = frontFigureFactory;
        this.canCreate = canCreate;
    }

    public ToggleButton getButton(){
        return button;
    }

    public void createShape (Point startPoint,Point endPoint, Color fillColor, Color lineColor, double lineWidth, PaintPane paintPane){
        if(button.isSelected() && canCreate.test(startPoint,endPoint)){
            Figure newFigure = backFigureFactory.get(startPoint,endPoint);
            FrontFigure newFrontFigure = frontFigureFactory.get(fillColor,lineColor,lineWidth);
            paintPane.addBackFigure(newFigure);
            paintPane.addFrontFigure(newFigure,newFrontFigure);
        }
    }
}
