package com.example.tpe_final_poo.frontend;

import com.example.tpe_final_poo.backend.model.*;
import com.example.tpe_final_poo.frontend.frontEndModel.FrontEllipse;
import com.example.tpe_final_poo.frontend.frontEndModel.FrontFigure;
import com.example.tpe_final_poo.frontend.frontEndModel.FrontLine;
import com.example.tpe_final_poo.frontend.frontEndModel.FrontRectangle;
import javafx.scene.control.ToggleButton;

import java.util.function.*;

public class NewShapeActionButton {//Esto deberia ser un Enum, despues lo vemos
    private ToggleButton button;
    PaintPane paintPane; //No queda muy lindo
    BackFigureFactory backFigureFactory;
    FrontFigureFactory frontFigureFactory;
//    FrontFigureFunction frontFigureFunction;
//    BackFigureFunction backFigureFunction;
    public BiPredicate<PaintPane,Point> canCreate = (paintPane,endPoint)->endPoint.getX() >= paintPane.startPoint.getX() && endPoint.getY() >= paintPane.startPoint.getY();

//    public NewShapeActionButton(ToggleButton button, PaintPane paintPane, BackFigureFunction backFigureFunction, FrontFigureFunction frontFigureFunction) {
//        this.button = button;
//        this.paintPane = paintPane;
//        this.frontFigureFunction = frontFigureFunction;
//        this.backFigureFunction = backFigureFunction;
//    }

    public NewShapeActionButton(ToggleButton button, PaintPane paintPane, BackFigureFactory backFigureFactory, FrontFigureFactory frontFigureFactory,BiPredicate<PaintPane,Point> canCreate) {
        this.button = button;
        this.paintPane = paintPane;
        this.backFigureFactory = backFigureFactory;
        this.frontFigureFactory = frontFigureFactory;
        this.canCreate = canCreate;
    }

    public ToggleButton getButton(){
        return button;
    }
//    public void setCanCreate(BiPredicate<PaintPane,Point> canCreate){
//        this.canCreate = canCreate;
//    }
    public void createShape(Point endPoint){
        if(button.isSelected() && canCreate.test(paintPane,endPoint)){
            Figure newFigure = backFigureFactory.get(paintPane.startPoint,endPoint);
            FrontFigure newFrontFigure = frontFigureFactory.get(paintPane.fillColor,paintPane.lineColor,paintPane.lineWidth);
            paintPane.canvasState.addFigure(newFigure);
            paintPane.frontFigureMap.put(newFigure.getId(),newFrontFigure);
            paintPane.deselectAll();
        }
    }
//    public enum FrontFigureFunction{
//        RECTANGLE((paintPane) -> new FrontRectangle(paintPane.fillColor,paintPane.lineColor, paintPane.lineWidth)),
//        ELLIPSE(paintPane-> new FrontEllipse(paintPane.fillColor,paintPane.lineColor, paintPane.lineWidth) ),
//        LINE((paintPlane)->new FrontLine(paintPlane.fillColor,paintPlane.lineColor,paintPlane.lineWidth));
//        private Function<PaintPane,FrontFigure> frontFigureFunction;
//        FrontFigureFunction(Function<PaintPane,FrontFigure> frontFigureFunction){
//            this.frontFigureFunction = frontFigureFunction;
//        }
//        public Function<PaintPane,FrontFigure> getFunction(){
//            return frontFigureFunction;
//        }
//    }
//    public enum BackFigureFunction{
//        RECTANGLE((endPoint,paintPane)->new Rectangle(paintPane.startPoint,endPoint)),
//        ELLIPSE((endPoint,paintPane)-> new Ellipse(paintPane.startPoint, endPoint)),
//        CIRCLE((endPoint,paintPane)->new Circle(paintPane.startPoint,  Math.abs(endPoint.getX() - paintPane.startPoint.getX()))),
//        LINE((endPoint,paintPane)-> new Line(paintPane.startPoint, endPoint)),
//        SQUARE((endPoint,paintPane)->new Square(paintPane.startPoint,endPoint));
//        private BiFunction<Point,PaintPane, Figure> backFigureFunction;
//
//        BackFigureFunction(BiFunction<Point, PaintPane, Figure> backFigureFunction) {
//            this.backFigureFunction = backFigureFunction;
//        }
//        public BiFunction<Point,PaintPane,Figure> getFunction(){
//            return backFigureFunction;
//        }
//    }
}
