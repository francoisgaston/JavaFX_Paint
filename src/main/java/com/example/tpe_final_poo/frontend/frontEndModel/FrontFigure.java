package com.example.tpe_final_poo.frontend.frontEndModel;

import com.example.tpe_final_poo.backend.model.Figure;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class FrontFigure{
    private Color fillColor;
    private Color lineColor;
    private double lineWidth;
    public FrontFigure (Color fillColor, Color lineColor, double lineWidth){
        this.fillColor = fillColor;
        this.lineColor = lineColor;
        this.lineWidth = lineWidth;
    }
    public void draw(GraphicsContext gc, Figure figure){
        gc.setStroke(lineColor);
        gc.setLineWidth(lineWidth);
        gc.setFill(fillColor);
    }
    public double getLineWidth(){
        return lineWidth;
    }
    public Color getFillColor(){
        return fillColor;
    }
    public Color getLineColor(){
        return lineColor;
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
