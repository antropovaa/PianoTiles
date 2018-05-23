package sample;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.sound.midi.MidiUnavailableException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

class Appearance {
    private Scene menuScene, mainScene, choiceScene;
    private Stage primaryStage;
    private File font = new File("src/resources/Arial Rounded Bold.ttf");
    private Piano piano = new Piano(85, 150);

    Appearance(Stage primaryStage) throws MidiUnavailableException, FileNotFoundException {
        this.primaryStage = primaryStage;
    }

    void setMenuScene() throws FileNotFoundException {
        Text welcome1 = new Text(220, 96, "PIANO TILES");
        welcome1.setFont(Font.loadFont(new FileInputStream(font), 57.45));
        Text welcome2 = new Text(222, 98, "PIANO TILES");
        welcome2.setFont(Font.loadFont(new FileInputStream(font), 57.45));
        welcome1.setFill(Color.WHITE);

        Text start = new Text(358, 231,"PLAY");
        start.setFont(Font.loadFont(new FileInputStream(font), 36));
        start.setOnMouseEntered(e -> start.setFill(Color.WHITE));
        start.setOnMouseExited(e -> start.setFill(Color.BLACK));
        start.setOnMouseClicked(e -> primaryStage.setScene(choiceScene));

        Text settings = new Text(311, 283,"SETTINGS");
        settings.setFont(Font.loadFont(new FileInputStream(font), 36));
        settings.setOnMouseEntered(e -> settings.setFill(Color.WHITE));
        settings.setOnMouseExited(e -> settings.setFill(Color.BLACK));
        //settings.setOnMouseClicked(e -> primaryStage.setScene(settingScene));

        Text exit = new Text(363, 335, "EXIT");
        exit.setFont(Font.loadFont(new FileInputStream(font), 36));
        exit.setOnMouseEntered(e -> exit.setFill(Color.WHITE));
        exit.setOnMouseExited(e -> exit.setFill(Color.BLACK));
        exit.setOnMouseClicked(e -> primaryStage.close());

        Pane menu = new Pane();
        menu.setStyle("-fx-background-color: #959181");
        menu.getChildren().addAll(welcome1, welcome2, start, settings, exit);
        menuScene = new Scene(menu, 800, 500);
    }

    void setChoiceScene() throws MidiUnavailableException, FileNotFoundException {
        Text choiceText = new Text(182, 190,"Choose the type of piano:");
        choiceText.setFont(Font.loadFont(new FileInputStream(font), 36));

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

        ChoiceBox choiceBox = new ChoiceBox(programs);
        choiceBox.setValue("Acoustic piano");
        choiceBox.getSelectionModel().selectedItemProperty().addListener((ChangeListener<String>) (ov, old_val, new_val) -> {
            for (int i = 0; i < programs.size(); i++)
                if (new_val.equals(programs.get(i)))
                    piano.changeProgram(0,i + 1);
        });
        choiceBox.setLayoutX(327);
        choiceBox.setLayoutY(220);

        Text back = new Text(10, 30,"< BACK");
        back.setFont(Font.loadFont(new FileInputStream(font), 24));
        back.setOnMouseEntered(e -> back.setFill(Color.WHITE));
        back.setOnMouseExited(e -> back.setFill(Color.BLACK));
        back.setOnMouseClicked(e -> primaryStage.setScene(menuScene));

        Text play = new Text(316, 300,"LET'S PLAY");
        play.setFont(Font.loadFont(new FileInputStream(font), 30));
        play.setOnMouseEntered(e -> play.setFill(Color.WHITE));
        play.setOnMouseExited(e -> play.setFill(Color.BLACK));
        play.setOnMouseClicked(e -> primaryStage.setScene(mainScene));

        Pane choice = new Pane();
        choice.setStyle("-fx-background-color: #959181");
        choice.getChildren().addAll(back, choiceText, choiceBox, play);

        choiceScene = new Scene(choice, 800, 500);
        primaryStage.setScene(menuScene);
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
                piano.synthesizer.close();
                piano.volume = 0;
            } else {
                try {
                    piano.synthesizer.open();
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
        octavesPane.setLayoutY(100);
        octavesPane.setLayoutX(120);

        Button menuButton = new Button("Menu");
        menuButton.setOnAction(e -> primaryStage.setScene(menuScene));

        ToggleButton recordButton = new ToggleButton("Record");
        recordButton.setSelected(false);
        recordButton.setOnAction(e -> {
            if (recordButton.isSelected())
                piano.setStatus(true);
            else {
                piano.setStatus(false);
            }
        });
        recordButton.setLayoutY(0);
        recordButton.setLayoutX(450);

        Button clearButton = new Button("Clear history");
        clearButton.setOnAction(e -> piano.clear());
        clearButton.setLayoutY(0);
        clearButton.setLayoutX(650);

        Button playSongButton = new Button("Play song");
        playSongButton.setOnAction(e -> piano.playSong());
        playSongButton.setLayoutX(550);
        playSongButton.setLayoutY(0);

        pane1.getChildren().addAll(piano.getKeyPane(), menuButton, muteButton, octavesPane, recordButton, clearButton, playSongButton);

        mainScene = new Scene(pane1, 800, 500);
        piano.addKeyEvent(mainScene);
    }
}
