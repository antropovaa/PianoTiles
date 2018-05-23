package sample;

import javafx.application.Application;

import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Appearance appearance = new Appearance(primaryStage);
        appearance.setMenuScene();
        appearance.setChoiceScene();
        appearance.setMainScene();
        primaryStage.setTitle("PIANO TILES");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
