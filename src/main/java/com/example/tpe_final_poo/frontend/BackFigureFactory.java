package com.example.tpe_final_poo.frontend;

import com.example.tpe_final_poo.backend.model.Figure;
import com.example.tpe_final_poo.backend.model.Point;

@FunctionalInterface
public interface BackFigureFactory {
    Figure get(Point topLeft, Point bottomRight);
}
