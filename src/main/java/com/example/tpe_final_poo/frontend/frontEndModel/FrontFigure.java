package com.example.tpe_final_poo.frontend.frontEndModel;

import com.example.tpe_final_poo.backend.model.Figure;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class FrontFigure{
    private static final Color SELECTED_BORDER_COLOR = Color.RED;
    private Color fillColor;
    private Color lineColor;
    private double lineWidth;
    private boolean isSelected = false;

    public FrontFigure (Color fillColor, Color lineColor, double lineWidth){
        this.fillColor = fillColor;
        this.lineColor = lineColor;
        this.lineWidth = lineWidth;
    }
    public void select(){
        isSelected = true;
    }
    public void deselect(){
        isSelected = false;
    }
    public void draw(GraphicsContext gc, Figure figure){
        gc.setStroke(lineColor);
        gc.setLineWidth(lineWidth);
        gc.setFill(fillColor);
        if(isSelected){
            gc.setStroke(SELECTED_BORDER_COLOR);
        }else{
            gc.setStroke(lineColor);
        }
    }
    public void setLineWidth(double lineWidth){
        this.lineWidth = lineWidth;
    }
    public void setFillColor(Color fillColor){
        this.fillColor = fillColor;
    }
    public void setLineColor(Color lineColor){
        this.lineColor = lineColor;
    }
}
