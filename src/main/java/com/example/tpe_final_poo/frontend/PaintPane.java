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

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

public class PaintPane extends BorderPane {
	//TODO: ver si dejamos a las variables package protected o hacemos getters
	//Funciones para obtener la figura
	Function<PaintPane,FrontFigure> getFrontRectanlge = (paintPane) -> new FrontRectangle(paintPane.fillColor,paintPane.lineColor, paintPane.lineWidth);
	// BackEnd
	CanvasState canvasState;

	// Canvas y relacionados
	Canvas canvas = new Canvas(800, 600);
	GraphicsContext gc = canvas.getGraphicsContext2D();
	Color lineColor = Color.BLACK;
	Color fillColor = Color.YELLOW;
	double lineWidth = 1;


	// Botones Barra Izquierda
	BiPredicate<PaintPane,Point> TopLeftToBottomRight = (paintPane, endPoint)->endPoint.getX() >= paintPane.startPoint.getX() && endPoint.getY() >= paintPane.startPoint.getY();
	ToggleButton selectionButton = new ToggleButton("Seleccionar");
	ToggleButton deleteButton = new ToggleButton("Eliminar");
	//Ahora quedaron un poco mejor los botones
	NewShapeActionButton rectangleButton = new NewShapeActionButton(new ToggleButton("Rectángulo"),this, Rectangle::new, FrontRectangle::new,TopLeftToBottomRight);
	//ToggleButton circleButton = new ToggleButton("Círculo");
	NewShapeActionButton circleButton = new NewShapeActionButton(new ToggleButton("Círculo"),this, Circle::new, FrontEllipse::new,TopLeftToBottomRight);
	//ToggleButton ellipseButton = new ToggleButton("Elipse");
	NewShapeActionButton ellipseButton = new NewShapeActionButton(new ToggleButton("Elipse"),this, Ellipse::new, FrontEllipse::new,TopLeftToBottomRight);
	//ToggleButton squareButton = new ToggleButton("Cuadrado");
	NewShapeActionButton squareButton = new NewShapeActionButton(new ToggleButton("Cuadrado"),this, Square::new, FrontRectangle::new,TopLeftToBottomRight);
	//ToggleButton LineButton = new ToggleButton("Linea");
	NewShapeActionButton lineButton = new NewShapeActionButton(new ToggleButton("Linea"),this, Line::new, FrontLine::new,(a,b)->true);
	ToggleButton moveToFront = new ToggleButton("Al Frente"); //Memorias de PI
	ToggleButton moveToBack = new ToggleButton("Al Fondo");
	ColorPicker fillColorPicker = new ColorPicker(fillColor);
	ColorPicker lineColorPicker = new ColorPicker(lineColor);
	Slider lineWidthSlider = new Slider(1,20,lineWidth);
	Point startPoint;

	Figure selectedFigure;
	List<NewShapeActionButton> newShapeActionButtonList = new ArrayList<>();
	StatusPane statusPane;

	Rectangle selectionRectangle = null;
	//Map que almacena como claves a los IDs de las figuras y como claves a las FrontFigures
	Map<Integer, FrontFigure> frontFigureMap = new HashMap<>();
	//para actualizar el texto cuando estoy sobre la figura
	public PaintPane(CanvasState canvasState, StatusPane statusPane) {
		this.canvasState = canvasState;
		this.statusPane = statusPane;
		NewShapeActionButton[] shapeButton = {rectangleButton,squareButton,circleButton,ellipseButton,lineButton};//TODO mejorar esto
		//lineButton.setCanCreate((a,b)->true);
		newShapeActionButtonList.addAll(Arrays.stream(shapeButton).toList());
		ToggleButton[] toolsArr = {selectionButton, rectangleButton.getButton(), circleButton.getButton(), ellipseButton.getButton(), squareButton.getButton(), lineButton.getButton(),moveToFront,moveToBack,deleteButton};
		ToggleGroup tools = new ToggleGroup();
		for (ToggleButton tool : toolsArr) {
			tool.setMinWidth(90);
			tool.setToggleGroup(tools);
			tool.setCursor(Cursor.HAND);

		}
		VBox buttonsBox = new VBox(10);
		buttonsBox.getChildren().addAll(toolsArr);
		buttonsBox.getChildren().addAll(fillColorPicker, lineColorPicker);
		buttonsBox.getChildren().add(lineWidthSlider);
		buttonsBox.setPadding(new Insets(5));
		buttonsBox.setStyle("-fx-background-color: #999");
		buttonsBox.setPrefWidth(100);
		gc.setLineWidth(1);
		moveToFront.setOnAction(even->{
			forEachSelectedFigure(canvasState::moveToFront);
		});
		moveToBack.setOnAction(event-> forEachSelectedFigure(canvasState::moveToBack));
		deleteButton.setOnAction(event-> forEachSelectedFigure(this::removeFigure));
		fillColorPicker.setOnAction(event-> {
			fillColor = fillColorPicker.getValue();
			forEachSelectedFigure(figure->frontFigureMap.get(figure.getId()).setFillColor(fillColor));
		});
		lineColorPicker.setOnAction(event-> {
			lineColor = lineColorPicker.getValue();
			forEachSelectedFigure(figure->frontFigureMap.get(figure.getId()).setLineColor(lineColor));
		});
		lineWidthSlider.setOnMouseDragged(event->{
			lineWidth = lineWidthSlider.getValue();
			forEachSelectedFigure(figure->frontFigureMap.get(figure.getId()).setLineWidth(lineWidth));
		});

		canvas.setOnMousePressed(event -> startPoint = new Point(event.getX(), event.getY()));

		canvas.setOnMouseReleased(event -> {
			Point endPoint = new Point(event.getX(), event.getY());
			if(startPoint == null) {
				return ;
			}
			for(NewShapeActionButton newShapeActionButton : newShapeActionButtonList){
					newShapeActionButton.createShape(endPoint);
			}
			if(selectionButton.isSelected()){
				selectionRectangle  = new Rectangle(startPoint,endPoint);
				for(Figure figure : canvasState.figures()){
					if (figure.isInRectangle(selectionRectangle)){
						//System.out.println("Seleccionada!");
						frontFigureMap.get(figure.getId()).select();
					}
				}
			}else{
				selectionRectangle = null;
			}
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
		canvas.setOnMouseClicked(event -> { //cambiar cuando hagamos lo de seleccion multiple
			if(selectionButton.isSelected()) {
				if(selectionRectangle==null){
					deselectAll();
				}
				Point eventPoint = new Point(event.getX(), event.getY());
				boolean found = false;
				StringBuilder label = new StringBuilder("Se seleccionó: ");
				//Hacer un metodo que vaya por todas las figuras y devuelva el Label
				//deselectAll();
				//TODO: tiene sentido que esto este aca, arreglarlo despues
				for (Figure figure : canvasState.figures()){
					if(figure.pointBelongs(eventPoint)) {
						found = true;
						selectedFigure = figure;
						label.append(figure);
					}
				}
				if (found) {
					statusPane.updateStatus(label.toString());
					frontFigureMap.get(selectedFigure.getId()).select();
				} else {
					selectedFigure = null;
					statusPane.updateStatus("Ninguna figura encontrada");
				}
				if(startPoint==null){
					deselectAll();
				}
				redrawCanvas();
			}else{
				deselectAll();
			}
			//redrawCanvas();
		});
		canvas.setOnMouseDragged(event -> {
			if(selectionButton.isSelected()) {
				Point eventPoint = new Point(event.getX(), event.getY());
				double diffX = (eventPoint.getX() - startPoint.getX()) / 100;
				double diffY = (eventPoint.getY() - startPoint.getY()) / 100;
//				if (selectedFigure !=null) {
//					selectedFigure.moveX(diffX);
//					selectedFigure.moveY(diffY);
//				}
				forEachSelectedFigure(figure->{
					figure.moveX(diffX);
					figure.moveY(diffY);
				});
				redrawCanvas();
			}
		});
		setLeft(buttonsBox);
		setRight(canvas);
	}
	void deselectAll(){
		for(FrontFigure figure : frontFigureMap.values()){
			figure.deselect();
		}
	}
	void removeFigure(Figure figure){
		canvasState.delete(figure);
		frontFigureMap.remove(figure.getId());
	}
	void redrawCanvas() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		//borra las figuras
		for(Figure figure : canvasState.figures()) {
			//{figure.id} = info
			// info.draw(gc,figure)
			frontFigureMap.get(figure.getId()).draw(gc,figure);
		}
	}
	public void forEachSelectedFigure(Consumer<Figure> consumer){
		for(Figure figure : canvasState.figures()){
			if(frontFigureMap.get(figure.getId()).isSelected()){
				consumer.accept(figure);
			}
		}
		redrawCanvas();
	}

}
