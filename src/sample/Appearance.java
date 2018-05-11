package sample;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.sound.midi.MidiUnavailableException;
import java.io.FileNotFoundException;

class Appearance {
    private Scene menuScene, mainScene, choiceScene;
    private Piano piano = new Piano();
    private Stage primaryStage;

    Appearance(Stage primaryStage) throws MidiUnavailableException, FileNotFoundException {
        this.primaryStage = primaryStage;
    }

    void setMenuScene() {
        Button startButton = new Button("Play");
        startButton.setOnAction(e -> primaryStage.setScene(choiceScene));
        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> primaryStage.close());
        Label welcomeLabel = new Label("Piano Tiles");

        GridPane menu = new GridPane();
        menu.setAlignment(Pos.CENTER);
        menu.setVgap(5);
        menu.add(welcomeLabel, 1, 0);
        menu.add(startButton, 1, 1);
        menu.add(exitButton, 1, 2);

        menu.setStyle("-fx-background-color: #959181");
        menuScene = new Scene(menu, 800, 500);
    }

    void setChoiceScene() throws MidiUnavailableException {
        Label choiceLabel = new Label("Choose the type of piano:");
        ObservableList<String> programs = FXCollections.observableArrayList(
                "Acoustic piano",
                "Bright piano",
                "Grand piano",
                "Honky-tonk piano",
                "Rhodes piano1",
                "Chorused piano2",
                "Harpsichord",
                "Clavinet"
        );

        ChoiceBox choiceBox = new ChoiceBox();
        choiceBox.setItems(programs);
        choiceBox.getSelectionModel().selectedItemProperty().addListener((ChangeListener<String>) (ov, old_val, new_val) -> {
            for (int i = 0; i < programs.size(); i++)
                if (new_val.equals(programs.get(i)))
                    piano.changeProgram(i + 1);
        });

        Button menuButton1 = new Button("Back to menu");
        menuButton1.setOnAction(e -> primaryStage.setScene(menuScene));

        Button playButton = new Button("Play");
        playButton.setOnAction(e -> primaryStage.setScene(mainScene));

        VBox choice = new VBox();
        choice.setAlignment(Pos.CENTER);
        choice.setSpacing(5);
        choice.setStyle("-fx-background-color: #959181");
        choice.getChildren().addAll(menuButton1, choiceLabel, choiceBox, playButton);

        choiceScene = new Scene(choice, 800, 500);
        primaryStage.setScene(menuScene);
        primaryStage.setTitle("Piano Tiles");
    }

    void setMainScene() throws FileNotFoundException, MidiUnavailableException {
        Pane pane1 = new Pane();
        pane1.setStyle("-fx-background-color: #959181");
        ToggleButton muteButton = new ToggleButton("Mute");
        muteButton.setSelected(false);
        muteButton.setLayoutX(350);
        muteButton.setLayoutY(0);
        muteButton.setOnAction(e -> {
            if (muteButton.isSelected()) {
                piano.synth.close();
                piano.volume = 0;
            } else {
                try {
                    piano.synth.open();
                } catch (MidiUnavailableException e1) {
                    e1.printStackTrace();
                }
                piano.volume = 100;
            }
        });

        ToggleButton[] octaves = new ToggleButton[7];
        ToggleGroup octavesButtons = new ToggleGroup();
        HBox octavesPane = new HBox();
        octavesPane.setSpacing(5);

        for (int i = 0; i < 7; i++) {
            int num = i + 1;
            octaves[i] = new ToggleButton("Octave " + num);
            octaves[i].setSelected(false);
            octaves[i].setToggleGroup(octavesButtons);
            octaves[i].setOnAction(e -> {
                if (octaves[num - 1].isSelected())
                    piano.setOctave(num);
            });
            octavesPane.getChildren().add(octaves[i]);
        }
        octavesPane.setLayoutY(460);
        octavesPane.setLayoutX(120);

        Button menuButton = new Button("Menu");
        menuButton.setOnAction(e -> primaryStage.setScene(menuScene));

        pane1.getChildren().addAll(piano.getKeyPane(), menuButton, muteButton, octavesPane);

        mainScene = new Scene(pane1, 800, 500);
        piano.keyEvent(mainScene);
    }
}
