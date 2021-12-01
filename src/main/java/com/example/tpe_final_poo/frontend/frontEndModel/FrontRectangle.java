package com.example.tpe_final_poo.frontend.frontEndModel;

import com.example.tpe_final_poo.backend.model.Figure;
import com.example.tpe_final_poo.backend.model.Rectangle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class FrontRectangle extends FrontFigure{

    public FrontRectangle(Color fillColor, Color lineColor, double lineWidth){
        super(fillColor, lineColor, lineWidth);
    }
    @Override
    public void draw(GraphicsContext gc, Figure figure){
        super.draw(gc,figure);
        gc.fillRect(figure.getTopLeft().getX(), figure.getTopLeft().getY(), figure.getWidth(), figure.getHeight());
        gc.strokeRect(figure.getTopLeft().getX(), figure.getTopLeft().getY(), figure.getWidth(), figure.getHeight());
    }
}
