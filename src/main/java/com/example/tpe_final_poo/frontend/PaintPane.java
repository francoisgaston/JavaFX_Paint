package com.example.tpe_final_poo.frontend;

import com.example.tpe_final_poo.backend.CanvasState;
import com.example.tpe_final_poo.backend.model.*;
import com.example.tpe_final_poo.frontend.ActionButton.NewShapeActionButton;
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
	private Figure selectedFigure = null;
	private StatusPane statusPane;
	boolean inFigure;
	private Rectangle selectionRectangle = null;
	private final String NOT_FIGURE_SELECTED = "Ninguna figura seleccionada";

	//Colecciones
	//Map que almacena como claves a los IDs de las figuras y como claves a las FrontFigures
	private Map<Integer, FrontFigure> frontFigureMap = new HashMap<>();
	//Set que almacena las figuras seleccionadas
	private Set<Figure> selectedFigures = new HashSet<>();
	//PaintPane: manejo de botones, figuras y status pane.
	public PaintPane(CanvasState canvasState, StatusPane statusPane) {
		this.canvasState = canvasState;
		this.statusPane = statusPane;
		NewShapeActionButton[] newShapeActionButtons = {rectangleButton,squareButton,circleButton,ellipseButton,lineButton};
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
			if(startPoint == null) return;
			for(NewShapeActionButton newShapeActionButton : newShapeActionButtons){
				newShapeActionButton.createShape(startPoint,endPoint,fillColor,lineColor,lineWidth,this);
				if(newShapeActionButton.isSelected()) deselectAll();
			}
			if(selectionButton.isSelected() && selectedFigures.isEmpty() ){
				//tengo que hacer un rectangulo solo si no tengo figuras seleccionadas
				selectionRectangle = new Rectangle(startPoint, new Point(event.getX(), event.getY()));
				StringBuilder label = new StringBuilder("Se seleccionó: ");
				for (Figure figure : canvasState.figures()) {
					if (figure.isInRectangle(selectionRectangle)) {
						selectedFigures.add(figure);
						frontFigureMap.get(figure.getId()).select();
						label.append(figure);
					}
				}
				if(!selectedFigures.isEmpty()){
					statusPane.updateStatus(label.toString());
				}else{
					statusPane.updateStatus(NOT_FIGURE_SELECTED);
				}
			}
			redrawCanvas();
		});
		canvas.setOnMouseClicked(event->{
			if(startPoint.equals(new Point(event.getX(),event.getY()))) { //si es un clic verdadero, y no es que sale de pressed
			// if(startPoint.closeTo(new Point(event.getX(),event.getY()))){
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
						//deselectAll();
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
				forEachSelectedFigure(figure -> {
					figure.moveX(diffX);
					figure.moveY(diffY);
				});
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
	//Métodos que usa newShapeActionButton para agregar las figuras al mapa
	public void addBackFigure(Figure figure){
		canvasState.addFigure(figure);
	}
	public void addFrontFigure(Figure figure, FrontFigure frontFigure){
		frontFigureMap.put(figure.getId(),frontFigure);
	}
}
