package com.example.tpe_final_poo.frontend.frontEndModel;

import com.example.tpe_final_poo.backend.model.Figure;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class FrontLine extends FrontFigure{
    private static final int LINE_WIDTH = 1;
    public FrontLine(Color fillColor, Color lineColor, double lineWidth){
        super(fillColor, lineColor, lineWidth);
    }
    @Override
    public void draw(GraphicsContext gc, Figure figure){
        super.draw(gc,figure);
        gc.setLineWidth(LINE_WIDTH);
        gc.strokeLine(figure.getTopLeft().getX(), figure.getTopLeft().getY(), figure.getBottomRight().getX(), figure.getBottomRight().getY());
    }
}
