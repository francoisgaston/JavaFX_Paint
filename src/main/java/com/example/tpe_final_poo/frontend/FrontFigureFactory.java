package com.example.tpe_final_poo.frontend;

import com.example.tpe_final_poo.frontend.frontEndModel.FrontFigure;
import javafx.scene.paint.Color;

@FunctionalInterface
public interface FrontFigureFactory {
    FrontFigure get(Color fillColor, Color lineColor, double lineWidth);
}
