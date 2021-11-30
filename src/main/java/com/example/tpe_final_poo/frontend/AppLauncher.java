package com.example.tpe_final_poo.frontend;

import com.example.tpe_final_poo.backend.CanvasState;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppLauncher extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		CanvasState canvasState = new CanvasState(); // BackEnd
		//CanvasState tiene las figuras
		MainFrame frame = new MainFrame(canvasState);
		Scene scene = new Scene(frame);
		primaryStage.setResizable(false);
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setOnCloseRequest(event -> System.exit(0));
	}

}
