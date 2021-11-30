package com.example.tpe_final_poo.frontend;

import com.example.tpe_final_poo.backend.CanvasState;
import javafx.scene.layout.VBox;

public class MainFrame extends VBox {

    public MainFrame(CanvasState canvasState) {
        getChildren().add(new AppMenuBar());
        //agrega el menu superior (archivo/ayuda)
        StatusPane statusPane = new StatusPane();
        //StatusPane es el texto de abajo
        getChildren().add(new PaintPane(canvasState, statusPane));
        getChildren().add(statusPane);
    }

}
