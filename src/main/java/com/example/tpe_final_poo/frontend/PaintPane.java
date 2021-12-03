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
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

public class PaintPane extends BorderPane {
	private CanvasState canvasState;

	// Canvas y relacionados
	private Canvas canvas = new Canvas(800, 600);
	private GraphicsContext gc = canvas.getGraphicsContext2D();
	private Color lineColor = Color.BLACK;
	private Color fillColor = Color.YELLOW;
	private double maxLineWidth = 50;
	private double lineWidth = 1;


	// Botones Barra Izquierda
	private BiPredicate<Point,Point> TopLeftToBottomRight = (startPoint, endPoint)->endPoint.getX() >= startPoint.getX() && endPoint.getY() >= startPoint.getY();
	private ToggleButton selectionButton = new ToggleButton("Seleccionar");
	private NewShapeActionButton rectangleButton = new NewShapeActionButton(new ToggleButton("Rectángulo"), Rectangle::new , FrontRectangle::new ,TopLeftToBottomRight);
	private NewShapeActionButton circleButton = new NewShapeActionButton(new ToggleButton("Círculo"), Circle::new, FrontEllipse::new,TopLeftToBottomRight);
	private NewShapeActionButton ellipseButton = new NewShapeActionButton(new ToggleButton("Elipse"), Ellipse::new, FrontEllipse::new,TopLeftToBottomRight);
	private NewShapeActionButton squareButton = new NewShapeActionButton(new ToggleButton("Cuadrado"), Square::new, FrontRectangle::new,TopLeftToBottomRight);
	private NewShapeActionButton lineButton = new NewShapeActionButton(new ToggleButton("Linea"), Line::new, FrontLine::new,(a,b)->true);
	private ToggleButton deleteButton = new ToggleButton("Borrar");
	private ToggleButton moveToFront = new ToggleButton("Al Frente");
	private ToggleButton moveToBack = new ToggleButton("Al Fondo");
	private ColorPicker fillColorPicker = new ColorPicker(fillColor);
	private ColorPicker lineColorPicker = new ColorPicker(lineColor);
	private Slider lineWidthSlider = new Slider(1,maxLineWidth,lineWidth);
	private Point startPoint;
	//private Figure selectedFigure;
	private Figure selectedFigure = null;
	private List<NewShapeActionButton> newShapeActionButtonList = new ArrayList<>();
	private StatusPane statusPane;
	boolean inFigure;
	private Rectangle selectionRectangle = null;
	private final String NOT_FIGURE_SELECTED = "Ninguna figura seleccionada";

	//Colecciones
	//Map que almacena como claves a los IDs de las figuras y como claves a las FrontFigures
	private Map<Integer, FrontFigure> frontFigureMap = new HashMap<>();
	//Set que almacena las figuras seleccionadas
	private Set<Figure> selectedFigures = new HashSet<>();

	//para actualizar el texto cuando estoy sobre la figura
	//PaintPane: manejo de botones, figuras y status pane.
	public PaintPane(CanvasState canvasState, StatusPane statusPane) {
		this.canvasState = canvasState;
		this.statusPane = statusPane;
		NewShapeActionButton[] shapeButton = {rectangleButton,squareButton,circleButton,ellipseButton,lineButton};//TODO mejorar es
		newShapeActionButtonList.addAll(Arrays.stream(shapeButton).toList());
		ToggleButton[] toolsArr = {selectionButton, rectangleButton.getButton(), circleButton.getButton(), squareButton.getButton(), ellipseButton.getButton(), lineButton.getButton(), deleteButton ,moveToFront, moveToBack};
		ToggleGroup tools = new ToggleGroup();
		for (ToggleButton tool : toolsArr) {
			tool.setMinWidth(90);
			tool.setToggleGroup(tools);
			tool.setCursor(Cursor.HAND);
			tool.setStyle("-fx-font-size:12");
		}
		//Creacion de la seccion de botones
		VBox buttonsBox = new VBox(10);
		buttonsBox.setPadding(new Insets(5));
		buttonsBox.setStyle("-fx-background-color: #999");
		buttonsBox.setPrefWidth(100);
		buttonsBox.getChildren().addAll(toolsArr);
		//Configuracion de botones
		buttonsBox.getChildren().add(new Label("Borde"));
		lineWidthSlider.setMajorTickUnit(maxLineWidth/2);
		lineWidthSlider.setShowTickMarks(true);
		lineWidthSlider.setShowTickLabels(true);
		buttonsBox.getChildren().add(lineWidthSlider);
		buttonsBox.getChildren().add(lineColorPicker);
		buttonsBox.getChildren().add(new Label("Relleno"));
		buttonsBox.getChildren().addAll(fillColorPicker);

		gc.setLineWidth(1);
		moveToFront.setOnAction(event -> doIfAnyFigureSelected(canvasState::moveToFront));
		moveToBack.setOnAction(event -> doIfAnyFigureSelected(canvasState::moveToBack));
		//deleteButton.setOnAction(event-> forEachSelectedFigure(this::removeFigure));
		//Alerta para cuando clickeo borrar sin haber seleccionado algo
		deleteButton.setOnAction(event -> { doIfAnyFigureSelected(this::removeFigure);
			selectedFigures.clear(); //Hay que hacerlo aca para no tener una ConcurrentModificationException
		});
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

		canvas.setOnMousePressed(event -> {
			startPoint = new Point(event.getX(), event.getY());
			inFigure = belongSelectedFigure(startPoint);
		});

		canvas.setOnMouseReleased(event -> {
			Point endPoint = new Point(event.getX(), event.getY());
			if(startPoint == null) {
				return ;
			}
			for(NewShapeActionButton newShapeActionButton : newShapeActionButtonList){
					if(newShapeActionButton.createShape(startPoint,endPoint,fillColor,lineColor,lineWidth,this)){
						deselectAll();
					}
			}
			System.out.println("Holaa " + selectedFigures);
			if(selectionButton.isSelected() && selectedFigures.isEmpty() ){//tengo que hacer un rectangulo solo si no tengo figuras seleccionadas
				System.out.println("Entre");
				selectionRectangle = new Rectangle(startPoint, new Point(event.getX(), event.getY()));
				StringBuilder label = new StringBuilder("Se seleccionó: ");
				for (Figure figure : canvasState.figures()) {
					if (figure.isInRectangle(selectionRectangle)) {
						System.out.println("Estoy dentro del rectangulo");
						selectedFigures.add(figure);
						frontFigureMap.get(figure.getId()).select();
						label.append(figure);
					}
				}
				if(!selectedFigures.isEmpty()){
					statusPane.updateStatus(label.toString());
				}else{
					selectionRectangle = null;
					statusPane.updateStatus(NOT_FIGURE_SELECTED);
				}

			}
//			if(selectionButton.isSelected()) {
//				StringBuilder label = new StringBuilder("Se seleccionó: ");
//				if (startPoint.equals(endPoint)) {//Hace clic, medio
//					for (Figure figure : canvasState.figures()) {
//						if (figure.pointBelongs(endPoint)) {
//							selectedFigure = figure;
//						}
//					}
//					deselectAll();
//					if (selectedFigure != null) {
//						//deselectAll();
//						label.append(selectedFigure);
//						selectedFigures.add(selectedFigure);
//						statusPane.updateStatus(label.toString());
//						frontFigureMap.get(selectedFigure.getId()).select();
//					} else {
//						//deselectAll();
//						statusPane.updateStatus(NOT_FIGURE_SELECTED);
//					}
//				}else { //crea un rectangulo
//					if (!inFigure) {
//						deselectAll();
//						selectionRectangle = new Rectangle(startPoint, endPoint);
//						for (Figure figure : canvasState.figures()) {
//							if (figure.isInRectangle(selectionRectangle)) {
//								selectedFigures.add(figure);
//								frontFigureMap.get(figure.getId()).select();
//								label.append(figure);
//							}
//						}
//						statusPane.updateStatus(label.toString());
//					}else{
//						statusPane.updateStatus(NOT_FIGURE_SELECTED);
//					}
//				}
//			}
			redrawCanvas();
		});
		canvas.setOnMouseClicked(event->{
			if(selectionRectangle == null && !selectedFigures.isEmpty()){
				deselectAll();
			}
			if(selectionButton.isSelected()){
				StringBuilder label = new StringBuilder("Se seleccionó: ");
				Point eventPoint = new Point(event.getX(), event.getY());
				Figure selectedFigure = null;
				for(Figure figure : canvasState.figures()){
					if(figure.pointBelongs(eventPoint)){
						selectedFigure = figure;
					}
				}
				if(selectedFigure!=null){
					//deselectAll();
					label.append(selectedFigure);
					frontFigureMap.get(selectedFigure.getId()).select();
					selectedFigures.add(selectedFigure);
				}else{
					statusPane.updateStatus(NOT_FIGURE_SELECTED);
				}
				System.out.println("click" + selectedFigures);
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
					label.append(figure);
				}
			}
			if(found) {
				statusPane.updateStatus(label.toString());
			} else {
				statusPane.updateStatus(eventPoint.toString());
			}
		});
		// Mouse Clickeado
//		canvas.setOnMouseClicked(event->{
//			//Any figure selected
//			boolean onFigure = false;
//			Point eventPoint = new Point(event.getX(), event.getY());
//			for(Figure figure : canvasState.figures()){
//				if(figure.pointBelongs(eventPoint)){
//					onFigure = true;
//				}
//			}
//			if(!onFigure){
//				deselectAll();
//			}
//		});
		//canvas.setOnMouseClicked(event -> { //cambiar cuando hagamos lo de seleccion multiple
/*
			Point eventPoint = new Point(event.getX(), event.getY());
			if(selectionButton.isSelected()){
				if(startPoint!= null && startPoint.equals(eventPoint)){//selecciona solo una
					StringBuilder label = new StringBuilder("Se seleccionó: ");
					Figure selectedFigure = null;
					for(Figure figure : canvasState.figures()){
						if(figure.pointBelongs(eventPoint)){
							selectedFigure = figure;
							label.append(figure);
						}
					}
					if(selectedFigure!=null){
						statusPane.updateStatus(label.toString());
						frontFigureMap.get(selectedFigure.getId()).select();
					}else{
						statusPane.updateStatus("Ninguna figura seleccionada");
					}
				}
				redrawCanvas();
				*/
	//		}
//			if(selectionButton.isSelected()) {
////				if(selectionRectangle==null){
////					deselectAll();
////				}
//				Point eventPoint = new Point(event.getX(), event.getY());
//				boolean found = false;
//				StringBuilder label = new StringBuilder("Se seleccionó: ");
//				//Hacer un metodo que vaya por todas las figuras y devuelva el Label
//				//deselectAll();
//				//TODO: tiene sentido que esto este aca, arreglarlo despues
//				for (Figure figure : canvasState.figures()){
//					if(figure.pointBelongs(eventPoint)) {
//						found = true;
//						selectedFigure = figure;
//						label.append(figure);
//					}
//				}
//				if (found) {
//					statusPane.updateStatus(label.toString());
//					frontFigureMap.get(selectedFigure.getId()).select();
//				} else {
//					selectedFigure = null;
//					statusPane.updateStatus("Ninguna figura encontrada");
//				}
////				if(startPoint==null){
////					deselectAll();
////				}
//				redrawCanvas();
//			}else{
//				deselectAll();
//			}
			//redrawCanvas();
		//});
		canvas.setOnMouseDragged(event -> {
			if(selectionButton.isSelected()) {
				double diffX = (event.getX() - startPoint.getX()) / 100;
				double diffY = (event.getY() - startPoint.getY()) / 100;
//				if (inFigure) { //Se mueve solo si se inicia el movimiento dentro de la figura
				forEachSelectedFigure(figure -> {
					figure.moveX(diffX);
					figure.moveY(diffY);
				});
//				}
				redrawCanvas();
			}
		});
		setLeft(buttonsBox);
		setRight(canvas);
	}
	private void deselectAll(){
		for(Figure figure : selectedFigures){
			frontFigureMap.get(figure.getId()).deselect();
		}
		selectedFigures.clear();
	}
	private void removeFigure(Figure figure){
		canvasState.delete(figure);
		frontFigureMap.remove(figure.getId());
	}
	private void redrawCanvas() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		//borra las figuras
		for(Figure figure : canvasState.figures()) {
			//{figure.id} = info
			// info.draw(gc,figure)
			frontFigureMap.get(figure.getId()).draw(gc,figure);
		}
	}
	private void forEachSelectedFigure(Consumer<Figure> consumer){
		for(Figure figure : selectedFigures){
			consumer.accept(figure);
		}
		redrawCanvas();
	}
	private void doIfAnyFigureSelected(Consumer<Figure> consumer){
		if(selectedFigures.isEmpty()){
			statusPane.updateStatus(NOT_FIGURE_SELECTED);
		}else{
			forEachSelectedFigure(consumer);
		}
	}
	private boolean belongSelectedFigure(Point startPoint){
		for(Figure figure :selectedFigures){
			if(figure.pointBelongs(startPoint)){
				return true;
			}
		}
		return false;
	}
	//TODO: preguntar si el rectángulo imaginario debe poder dibujarse en todas las direcciones o solo de izquierda a derecha y arriba a abajo

	//Métodos que usa newShapeActionButton para agregar las figuras al mapa
	public void addBackFigure(Figure figure){
		canvasState.addFigure(figure);
	}
	public void addFrontFigure(Figure figure, FrontFigure frontFigure){
		frontFigureMap.put(figure.getId(),frontFigure);
	}
}
