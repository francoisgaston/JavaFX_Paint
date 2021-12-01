package com.example.tpe_final_poo.frontend;

import com.example.tpe_final_poo.backend.CanvasState;
import com.example.tpe_final_poo.backend.model.*;
import com.example.tpe_final_poo.frontend.frontEndModel.FrontEllipse;
import com.example.tpe_final_poo.frontend.frontEndModel.FrontFigure;
import com.example.tpe_final_poo.frontend.frontEndModel.FrontLine;
import com.example.tpe_final_poo.frontend.frontEndModel.FrontRectangle;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class PaintPane extends BorderPane {

	// BackEnd
	CanvasState canvasState;

	// Canvas y relacionados
	Canvas canvas = new Canvas(800, 600);
	GraphicsContext gc = canvas.getGraphicsContext2D();
	Color lineColor = Color.BLACK;
	Color fillColor = Color.YELLOW;
	private double lineWidth = 1;

	// Botones Barra Izquierda
	ToggleButton selectionButton = new ToggleButton("Seleccionar");
	ToggleButton rectangleButton = new ToggleButton("Rectángulo");
	ToggleButton circleButton = new ToggleButton("Círculo");
	ToggleButton ellipseButton = new ToggleButton("Elipse");
	ToggleButton squareButton = new ToggleButton("Cuadrado");
	ToggleButton LineButton = new ToggleButton("Linea");
	ColorPicker fillColorPicker = new ColorPicker();
	ColorPicker lineColorPicker = new ColorPicker();
	Slider slider = new Slider(0,100,0);
	Point startPoint;

	Figure selectedFigure;

	StatusPane statusPane;
	//Map que almacena como claves a los IDs de las figuras y como claves a las FrontFigures
	private Map<Integer, FrontFigure> frontFigureMap = new HashMap<>();
	//para actualizar el texto cuando estoy sobre la figura
	public PaintPane(CanvasState canvasState, StatusPane statusPane) {

		this.canvasState = canvasState;
		this.statusPane = statusPane;
		ToggleButton[] toolsArr = {selectionButton, rectangleButton, circleButton, ellipseButton, squareButton, LineButton};
		ToggleGroup tools = new ToggleGroup();
		for (ToggleButton tool : toolsArr) {
			tool.setMinWidth(90);
			tool.setToggleGroup(tools);
			tool.setCursor(Cursor.HAND);
		}
		VBox buttonsBox = new VBox(10);
		buttonsBox.getChildren().addAll(toolsArr);
		buttonsBox.getChildren().addAll(fillColorPicker, lineColorPicker);
		buttonsBox.getChildren().add(slider);
		buttonsBox.setPadding(new Insets(5));
		buttonsBox.setStyle("-fx-background-color: #999");
		buttonsBox.setPrefWidth(100);
		gc.setLineWidth(1);

		canvas.setOnMousePressed(event -> {
			startPoint = new Point(event.getX(), event.getY());
			deselectAll();
		});
		canvas.setOnMouseReleased(event -> {
			Point endPoint = new Point(event.getX(), event.getY());
			if(startPoint == null) {
				return ;
			}
			FrontFigure frontFigure = null;
			Figure newFigure = null;
			if(LineButton.isSelected()){
				newFigure = new Line(startPoint, endPoint);
				frontFigure = new FrontLine(fillColor,lineColor, lineWidth);
			}
			else {
				if (endPoint.getX() < startPoint.getX() || endPoint.getY() < startPoint.getY()) {
					return;
				}
				if (rectangleButton.isSelected()) {  //todo para mañana: mejorar
					newFigure = new Rectangle(startPoint, endPoint);
					frontFigure = new FrontRectangle(fillColor,lineColor, lineWidth);
					//crear FrontFigure, en este caso un FrontRectangle
				} else if (circleButton.isSelected()) {
					double circleRadius = Math.abs(endPoint.getX() - startPoint.getX());
					newFigure = new Circle(startPoint, circleRadius);
					frontFigure = new FrontEllipse(fillColor,lineColor, lineWidth);
				} else if (ellipseButton.isSelected()) {
					newFigure = new Ellipse(startPoint, endPoint);
					frontFigure = new FrontEllipse(fillColor,lineColor, lineWidth);
				} else if (squareButton.isSelected()) {
					newFigure = new Square(startPoint, endPoint);
					frontFigure = new FrontRectangle(fillColor,lineColor, lineWidth);
				} else {
					return;
				}
			}
			canvasState.addFigure(newFigure);
			frontFigureMap.put(newFigure.getId(),frontFigure);
			//agreaga al mapa {id,FrontFigure}

			startPoint = null;
			redrawCanvas();
		});
		canvas.setOnMouseMoved(event -> {
			Point eventPoint = new Point(event.getX(), event.getY());
			boolean found = false;
			StringBuilder label = new StringBuilder();
			for(Figure figure : canvasState.figures()) {
				if(figure.pointBelongs(eventPoint)) {

					found = true;
					label.append(figure.toString());
				}
			}
			if(found) {
				statusPane.updateStatus(label.toString());
			} else {
				statusPane.updateStatus(eventPoint.toString());
			}
		});
		// Mouse Clickeado
		canvas.setOnMouseClicked(event -> {
			if(selectionButton.isSelected()) {
				Point eventPoint = new Point(event.getX(), event.getY());
				boolean found = false;
				StringBuilder label = new StringBuilder("Se seleccionó: ");
				//Hacer un metodo que vaya por todas las figuras y devuelva el Label
				for (Figure figure : canvasState.figures()) {
					if(figure.pointBelongs(eventPoint)) {
						found = true;
						frontFigureMap.get(figure.getId()).select();
						selectedFigure = figure;
						label.append(figure.toString());
					} else{
						frontFigureMap.get(figure.getId()).deselect();
					}
				}
				if (found) {
					statusPane.updateStatus(label.toString());
				} else {
					selectedFigure = null;
					statusPane.updateStatus("Ninguna figura encontrada");
				}
				redrawCanvas();
			}else{
					deselectAll();
			}
		});
		canvas.setOnMouseDragged(event -> {
			if(selectionButton.isSelected()) {
				Point eventPoint = new Point(event.getX(), event.getY());
				double diffX = (eventPoint.getX() - startPoint.getX()) / 100;
				double diffY = (eventPoint.getY() - startPoint.getY()) / 100;
				if (selectedFigure !=null) {
					selectedFigure.moveX(diffX);
					selectedFigure.moveY(diffY);
				}
				redrawCanvas();
			}
		});
		fillColorPicker.setOnAction(event-> {
			fillColor = fillColorPicker.getValue();
			for(Figure figure : canvasState.figures()){
				if(frontFigureMap.get(figure.getId()).isSelected()){
					frontFigureMap.get(figure.getId()).setFillColor(fillColor);
				}
			}
			redrawCanvas();
		});
		lineColorPicker.setOnAction(event-> {
			lineColor = lineColorPicker.getValue();
			for(Figure figure : canvasState.figures()){
				if(frontFigureMap.get(figure.getId()).isSelected()){
					frontFigureMap.get(figure.getId()).setLineColor(lineColor);
				}
			}
			redrawCanvas();
		});
		setLeft(buttonsBox);
		setRight(canvas);
	}
	void deselectAll(){
		for(FrontFigure figure : frontFigureMap.values()){
			figure.deselect();
		}
	}
	void redrawCanvas() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		//borra las figuras
		for(Figure figure : canvasState.figures()) {
			//{figure.id} = info
			// info.draw(gc,figure)
//			if(frontFigureMap.get(figure.getId()).getIsSelected()) {
//				//la seleccionada tiene borde rojo
//				frontFigureMap.get(figure.getId()).setLineColor(Color.RED);
//			} else {
//				frontFigureMap.get(figure.getId()).setLineColor(lineColor);
//			}
//			frontFigureMap.get(figure.getId()).setFillColor(fillColor);
			frontFigureMap.get(figure.getId()).draw(gc,figure);
			//crear metodos privados si puedo, getWidth() y esos

		}
	}


}
