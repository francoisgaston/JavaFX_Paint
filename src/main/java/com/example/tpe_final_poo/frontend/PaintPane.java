package com.example.tpe_final_poo.frontend;

import com.example.tpe_final_poo.backend.CanvasState;
import com.example.tpe_final_poo.backend.model.Circle;
import com.example.tpe_final_poo.backend.model.Figure;
import com.example.tpe_final_poo.backend.model.Point;
import com.example.tpe_final_poo.backend.model.Rectangle;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class PaintPane extends BorderPane {

	// BackEnd
	CanvasState canvasState;

	// Canvas y relacionados
	Canvas canvas = new Canvas(800, 600);
	GraphicsContext gc = canvas.getGraphicsContext2D();
	Color lineColor = Color.BLACK;
	Color fillColor = Color.YELLOW;

	// Botones Barra Izquierda
	ToggleButton selectionButton = new ToggleButton("Seleccionar");
	ToggleButton rectangleButton = new ToggleButton("Rectángulo");
	ToggleButton circleButton = new ToggleButton("Círculo");

	// Dibujar una figura
	Point startPoint;

	// Seleccionar una figura
	Figure selectedFigure;

	// StatusBar
	StatusPane statusPane;
	//para actualizar el texto cuando estoy sobre la figura
	public PaintPane(CanvasState canvasState, StatusPane statusPane) {
		this.canvasState = canvasState;
		this.statusPane = statusPane;
		ToggleButton[] toolsArr = {selectionButton, rectangleButton, circleButton};
		ToggleGroup tools = new ToggleGroup();
		for (ToggleButton tool : toolsArr) {
			tool.setMinWidth(90);
			tool.setToggleGroup(tools);
			tool.setCursor(Cursor.HAND);
		}
		VBox buttonsBox = new VBox(10);
		buttonsBox.getChildren().addAll(toolsArr);
		buttonsBox.setPadding(new Insets(5));
		buttonsBox.setStyle("-fx-background-color: #999");
		buttonsBox.setPrefWidth(100);
		gc.setLineWidth(1); //esto hay que cambiarlo cuando hacemos cada figura
		//cambia el ancho del borde
		canvas.setOnMousePressed(event -> {
			startPoint = new Point(event.getX(), event.getY());
		});
		canvas.setOnMouseReleased(event -> {
			Point endPoint = new Point(event.getX(), event.getY());
			if(startPoint == null) {
				return ;
			}
			if(endPoint.getX() < startPoint.getX() || endPoint.getY() < startPoint.getY()) {
				return ;
			}
			Figure newFigure = null;
			if(rectangleButton.isSelected()) {
				newFigure = new Rectangle(startPoint, endPoint);
			}
			else if(circleButton.isSelected()) {
				double circleRadius = Math.abs(endPoint.getX() - startPoint.getX());
				newFigure = new Circle(startPoint, circleRadius);
			} else {
				return ;
			}
			canvasState.addFigure(newFigure);
			startPoint = null;
			redrawCanvas();
		});
		canvas.setOnMouseMoved(event -> {
			Point eventPoint = new Point(event.getX(), event.getY());
			boolean found = false;
			StringBuilder label = new StringBuilder();
			for(Figure figure : canvasState.figures()) {
				if(figure.pointBelongs(eventPoint)) {
					//deberia ser un metodo de la clase Figure para cada figura
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

		canvas.setOnMouseClicked(event -> {
			if(selectionButton.isSelected()) {
				Point eventPoint = new Point(event.getX(), event.getY());
				boolean found = false;
				StringBuilder label = new StringBuilder("Se seleccionó: ");
				//Hacer un metodo que vaya por todas las figuras y devuelva el Label
				for (Figure figure : canvasState.figures()) {
					if(figure.pointBelongs(eventPoint)) {
						found = true;
						selectedFigure = figure;
						label.append(figure.toString());
					}
				}
				if (found) {
					statusPane.updateStatus(label.toString());
				} else {
					selectedFigure = null;
					statusPane.updateStatus("Ninguna figura encontrada");
				}
				redrawCanvas();
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
//				if(selectedFigure instanceof Rectangle) {//revisar
//					Rectangle rectangle = (Rectangle) selectedFigure;
//					//rectangle.moveX(diffX);
//					rectangle.getTopLeft().x += diffX;
//					rectangle.getBottomRight().x += diffX;
//					//rectangle.moveY(diffY);
//					rectangle.getTopLeft().y += diffY;
//					rectangle.getBottomRight().y += diffY;
//				} else if(selectedFigure instanceof Circle) {//Usar Movable Figure
//					Circle circle = (Circle) selectedFigure;
//					circle.getCenterPoint().x += diffX;
//					circle.getCenterPoint().y += diffY;
//				}
				redrawCanvas();
			}
		});
		setLeft(buttonsBox);
		setRight(canvas);
	}

	void redrawCanvas() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		//borra las figuras
		for(Figure figure : canvasState.figures()) {
			if(figure == selectedFigure) {
				//la seleccionada tiene borde rojo
				gc.setStroke(Color.RED);
			} else {
				gc.setStroke(lineColor);
			}
			gc.setFill(fillColor);
			//crear metodos privados si puedo, getWidth() y esos
			if(figure instanceof Rectangle) {
				Rectangle rectangle = (Rectangle) figure;
//				gc.fillRect(rectangle.getTopLeft().getX(), rectangle.getTopLeft().getY(),
//						Math.abs(rectangle.getTopLeft().getX() - rectangle.getBottomRight().getX()), Math.abs(rectangle.getTopLeft().getY() - rectangle.getBottomRight().getY()));
				gc.fillRect(rectangle.getTopLeft().getX(), rectangle.getTopLeft().getY(), rectangle.getWidth(), rectangle.getHeight());
//				gc.strokeRect(rectangle.getTopLeft().getX(), rectangle.getTopLeft().getY(),
//						Math.abs(rectangle.getTopLeft().getX() - rectangle.getBottomRight().getX()), Math.abs(rectangle.getTopLeft().getY() - rectangle.getBottomRight().getY()));
				gc.strokeRect(rectangle.getTopLeft().getX(), rectangle.getTopLeft().getY(), rectangle.getWidth(), rectangle.getHeight());
			} else if(figure instanceof Circle) {
				Circle circle = (Circle) figure;
				double diameter = circle.getRadius() * 2;
				gc.fillOval(circle.getCenterPoint().getX() - circle.getRadius(), circle.getCenterPoint().getY() - circle.getRadius(), diameter, diameter);
				gc.strokeOval(circle.getCenterPoint().getX() - circle.getRadius(), circle.getCenterPoint().getY() - circle.getRadius(), diameter, diameter);
			}
		}
	}

//	boolean figureBelongs(Figure figure, Point eventPoint) {//MALLL
//		boolean found = false;
//		if(figure instanceof Rectangle) {
//			Rectangle rectangle = (Rectangle) figure;
//			found = eventPoint.getX() > rectangle.getTopLeft().getX() && eventPoint.getX() < rectangle.getBottomRight().getX() &&
//					eventPoint.getY() > rectangle.getTopLeft().getY() && eventPoint.getY() < rectangle.getBottomRight().getY();
//		} else if(figure instanceof Circle) {
//			Circle circle = (Circle) figure;
//			found = Math.sqrt(Math.pow(circle.getCenterPoint().getX() - eventPoint.getX(), 2) +
//					Math.pow(circle.getCenterPoint().getY() - eventPoint.getY(), 2)) < circle.getRadius();
//		}
//		return found;
//	}

}
