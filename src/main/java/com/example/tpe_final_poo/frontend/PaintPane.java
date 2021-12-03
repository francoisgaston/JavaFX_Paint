package com.example.tpe_final_poo.frontend;

import com.example.tpe_final_poo.backend.CanvasState;
import com.example.tpe_final_poo.backend.model.*;
import com.example.tpe_final_poo.frontend.ActionButton.NewFigureActionButton;
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

public class PaintPane extends BorderPane {
	private final CanvasState canvasState;

	// Canvas y relacionados
	private final Canvas canvas = new Canvas(800, 600);
	private final GraphicsContext gc = canvas.getGraphicsContext2D();
	private Color lineColor = Color.BLACK;
	private Color fillColor = Color.YELLOW;
	private static final double MAX_LINE_WIDTH = 50;
	private double lineWidth = 1;


	// Botones Barra Izquierda
	private static final  BiPredicate<Point,Point> TOP_LEFT_TO_BOTTOM_RIGHT = (startPoint, endPoint)->endPoint.getX() >= startPoint.getX() && endPoint.getY() >= startPoint.getY();
	private final ToggleButton selectionButton = new ToggleButton("Seleccionar");
	private final NewFigureActionButton rectangleButton = new NewFigureActionButton("Rectángulo", Rectangle::new , FrontRectangle::new , TOP_LEFT_TO_BOTTOM_RIGHT);
	private final NewFigureActionButton circleButton = new NewFigureActionButton("Círculo", Circle::new, FrontEllipse::new, TOP_LEFT_TO_BOTTOM_RIGHT);
	private final NewFigureActionButton ellipseButton = new NewFigureActionButton("Elipse", Ellipse::new, FrontEllipse::new, TOP_LEFT_TO_BOTTOM_RIGHT);
	private final NewFigureActionButton squareButton = new NewFigureActionButton("Cuadrado", Square::new, FrontRectangle::new, TOP_LEFT_TO_BOTTOM_RIGHT);
	private final NewFigureActionButton lineButton = new NewFigureActionButton("Linea", Line::new, FrontLine::new,(a, b)->true);
	private final ToggleButton deleteButton = new ToggleButton("Borrar");
	private final ToggleButton moveToFront = new ToggleButton("Al Frente");
	private final ToggleButton moveToBack = new ToggleButton("Al Fondo");
	private final ColorPicker fillColorPicker = new ColorPicker(fillColor);
	private final ColorPicker lineColorPicker = new ColorPicker(lineColor);
	private final Slider lineWidthSlider = new Slider(1, MAX_LINE_WIDTH,lineWidth);
	private Point startPoint;
	//private final Figure selectedFigure = null;
	private final StatusPane statusPane;
	private final String NOT_FIGURE_SELECTED = "Ninguna figura seleccionada";

	//Colecciones
	//Map que almacena como claves a los ID de las figuras y como claves a las FrontFigures
	private final Map<Integer, FrontFigure> frontFigureMap = new HashMap<>();
	//Set que almacena las figuras seleccionadas
	private final Set<Figure> selectedFigures = new TreeSet<>();
	//PaintPane: manejo de botones, figuras y status pane.
	public PaintPane(CanvasState canvasState, StatusPane statusPane) {
		this.canvasState = canvasState;
		this.statusPane = statusPane;
		NewFigureActionButton[] newFigureActionButtons = {rectangleButton,squareButton,circleButton,ellipseButton,lineButton};
		ToggleButton[] toolsArr = {selectionButton, rectangleButton, circleButton, squareButton, ellipseButton, lineButton, deleteButton ,moveToFront, moveToBack};
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
		lineWidthSlider.setMajorTickUnit(MAX_LINE_WIDTH /2);
		lineWidthSlider.setShowTickMarks(true);
		lineWidthSlider.setShowTickLabels(true);
		buttonsBox.getChildren().add(lineWidthSlider);
		buttonsBox.getChildren().add(lineColorPicker);
		buttonsBox.getChildren().add(new Label("Relleno"));
		buttonsBox.getChildren().addAll(fillColorPicker);
		setLeft(buttonsBox);
		setRight(canvas);
		gc.setLineWidth(1);
		moveToFront.setOnAction(event -> doIfAnyFigureSelected(canvasState::moveToFront));
		moveToBack.setOnAction(event -> doIfAnyFigureSelected(canvasState::moveToBack));
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
		});

		canvas.setOnMouseReleased(event -> {
			Point endPoint = new Point(event.getX(), event.getY());
			if(startPoint == null) return;
			for(NewFigureActionButton newFigureActionButton : newFigureActionButtons){
				//crea la figura si el boton esta seleccionado y cumple la condicion canCreate pasada en el constructor del boton
				newFigureActionButton.createFigure(endPoint,this);
				if(newFigureActionButton.isSelected()) deselectAll();
			}
			if(selectionButton.isSelected() && selectedFigures.isEmpty() ){
				//tengo que hacer un rectangulo solo si no tengo figuras seleccionadas
				Rectangle selectionRectangle = new Rectangle(startPoint, new Point(event.getX(), event.getY()));
				StringBuilder label = new StringBuilder("Se seleccionó: ");
				for (Figure figure : canvasState.figures()) {
					if (figure.isInRectangle(selectionRectangle)) {
						selectedFigures.add(figure);
						frontFigureMap.get(figure.getId()).select();
						label.append(figure);
					}
				}
				if(!selectedFigures.isEmpty()){
					//si se selecciono alguna figura
					statusPane.updateStatus(label.toString());
				}else{
					statusPane.updateStatus(NOT_FIGURE_SELECTED);
				}
			}
			redrawCanvas();
		});
		canvas.setOnMouseClicked(event->{
			if(startPoint.equals(new Point(event.getX(),event.getY()))) {
				//si es un clic estático.
				//Nota: Hay un caso especial con esta idea y es que en el caso donde se presiona el mouse (se actualiza startPoint)
				//y luego se mueve mientras se mantiene presionado y finalmente se suelta en el mismo punto donde se presiono,
				//Se cumple la condicion pero el click no es estatico. No consideramos este caso por la precision de la pantalla
				deselectAll();
				if (selectionButton.isSelected()) {
					StringBuilder label = new StringBuilder("Se seleccionó: ");
					Point eventPoint = new Point(event.getX(), event.getY());
					Figure selectedFigure = null;
					for (Figure figure : canvasState.figures()) {
						if (figure.pointBelongs(eventPoint)) {
							selectedFigure = figure;
						}
					}
					if (selectedFigure != null) {
						//si se selecciono alguna figura
						label.append(selectedFigure);
						frontFigureMap.get(selectedFigure.getId()).select();
						selectedFigures.add(selectedFigure);
						statusPane.updateStatus(label.toString());
					} else {
						statusPane.updateStatus(NOT_FIGURE_SELECTED);
					}
				}
				redrawCanvas();
			}
		});
		canvas.setOnMouseMoved(event -> {
			Point eventPoint = new Point(event.getX(), event.getY());
			boolean found = false;
			StringBuilder label = new StringBuilder();
			//agrega la informacion de la figura donde esta el mouse
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
		canvas.setOnMouseDragged(event -> {
			if(selectionButton.isSelected()) {
				double diffX = (event.getX() - startPoint.getX()) / 100;
				double diffY = (event.getY() - startPoint.getY()) / 100;
				//mueve todas las figuras seleccionadas
				forEachSelectedFigure(figure -> {
					figure.moveX(diffX);
					figure.moveY(diffY);
				});
				redrawCanvas();
			}
		});
	}
	private void deselectAll(){
		for(Figure figure : selectedFigures) {
			frontFigureMap.get(figure.getId()).deselect();
		}
		selectedFigures.clear();
	}
	private void removeFigure(Figure figure){ //para deleteButton
		canvasState.delete(figure);
		frontFigureMap.remove(figure.getId());
	}
	private void redrawCanvas() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		for(Figure figure : canvasState.figures()) {
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
	//Métodos que usa newShapeActionButton para agregar las figuras al mapa
	public void addFigure(Figure figure, FrontFigure frontFigure){
		canvasState.addFigure(figure);
		frontFigureMap.put(figure.getId(),frontFigure);
	}
	public Point getStartPoint() {
		return startPoint;
	}

	public Color getLineColor() {
		return lineColor;
	}

	public Color getFillColor() {
		return fillColor;
	}

	public double getLineWidth() {
		return lineWidth;
	}
}
