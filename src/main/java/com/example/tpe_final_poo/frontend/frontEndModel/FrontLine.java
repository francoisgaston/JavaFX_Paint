package com.example.tpe_final_poo.frontend.frontEndModel;

import com.example.tpe_final_poo.backend.model.Figure;
import com.example.tpe_final_poo.backend.model.Line;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class FrontLine extends FrontFigure{

    public FrontLine(Color fillColor, Color lineColor, double lineWidth){
        super(fillColor, lineColor, lineWidth);
    }
    @Override
    public void draw(GraphicsContext gc, Figure figure){
        super.draw(gc,figure);
        gc.setLineWidth(1);
        gc.strokeLine(figure.getTopLeft().getX(), figure.getTopLeft().getY(), figure.getBottomRight().getX(), figure.getBottomRight().getY());
    }
}
