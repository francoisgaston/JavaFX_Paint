package com.example.tpe_final_poo.frontend.ActionButton;

import com.example.tpe_final_poo.backend.model.*;
import com.example.tpe_final_poo.frontend.PaintPane;
import com.example.tpe_final_poo.frontend.frontEndModel.FrontFigure;
import javafx.scene.control.ToggleButton;

import java.util.function.*;

public class NewFigureActionButton extends ToggleButton{
    private final BackFigureFactory backFigureFactory; //crea la figura del back
    private final FrontFigureFactory frontFigureFactory;//crea la figura del front
    public BiPredicate<Point,Point> canCreate;

    public NewFigureActionButton(String text, BackFigureFactory backFigureFactory, FrontFigureFactory frontFigureFactory, BiPredicate<Point,Point> canCreate) {
        super(text);
        this.backFigureFactory = backFigureFactory;
        this.frontFigureFactory = frontFigureFactory;
        this.canCreate = canCreate;
    }
    public void createFigure(Point endPoint, PaintPane paintPane){
        //si el boton esta seleccionado y los puntos cumplen canCreate
        if(isSelected() && canCreate.test(paintPane.getStartPoint(),endPoint)){
            Figure newFigure = backFigureFactory.get(paintPane.getStartPoint(),endPoint);
            FrontFigure newFrontFigure = frontFigureFactory.get(paintPane.getFillColor(),paintPane.getLineColor(),paintPane.getLineWidth());
            //agrega las figuras a PaintPane
            paintPane.addFigure(newFigure,newFrontFigure);
        }
    }
}
