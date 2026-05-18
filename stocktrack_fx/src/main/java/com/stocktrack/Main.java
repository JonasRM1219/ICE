package com.stocktrack;

import com.stocktrack.controllers.WelcomeController;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        new WelcomeController().show(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
