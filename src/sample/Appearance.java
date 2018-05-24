package sample;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private Scene menuScene, mainScene, settingsScene;
    private Stage primaryStage;
    private File font = new File("src/resources/Arial Rounded Bold.ttf");
    private Color gray = Color.rgb(23, 23, 23);
    private Color light = Color.rgb(196, 196, 179);
    private Piano piano1 = new Piano(11, 140);

    Appearance(Stage primaryStage) throws MidiUnavailableException, FileNotFoundException {
        this.primaryStage = primaryStage;
    }

    void setMenuScene() throws FileNotFoundException {
        Text welcome1 = new Text(318, 96, "PIANO TILES");
        welcome1.setFont(Font.loadFont(new FileInputStream(font), 57.45));
        Text welcome2 = new Text(316, 98, "PIANO TILES");
        welcome2.setFont(Font.loadFont(new FileInputStream(font), 57.45));
        welcome2.setFill(gray);
        welcome1.setFill(Color.WHITE);

        Text version = new Text(688, 98, "v.1.0");
        version.setFont(Font.loadFont(new FileInputStream(font), 18));
        version.setFill(light);

        Text start = new Text(452, 231, "PLAY");
        start.setFill(gray);
        start.setFont(Font.loadFont(new FileInputStream(font), 36));

        start.setOnMouseEntered(e -> start.setFill(Color.WHITE));
        start.setOnMouseExited(e -> start.setFill(gray));
        start.setOnMouseClicked(e -> primaryStage.setScene(mainScene));

        Text settings = new Text(407, 283, "SETTINGS");
        settings.setFont(Font.loadFont(new FileInputStream(font), 36));
        settings.setFill(gray);
        settings.setOnMouseEntered(e -> settings.setFill(Color.WHITE));
        settings.setOnMouseExited(e -> settings.setFill(gray));
        settings.setOnMouseClicked(e -> primaryStage.setScene(settingsScene));

        Text exit = new Text(458, 335, "EXIT");
        exit.setFont(Font.loadFont(new FileInputStream(font), 36));
        exit.setFill(gray);
        exit.setOnMouseEntered(e -> exit.setFill(Color.WHITE));
        exit.setOnMouseExited(e -> exit.setFill(gray));
        exit.setOnMouseClicked(e -> primaryStage.close());

        Pane menu = new Pane();
        menu.setStyle("-fx-background-color: #959181");
        menu.getChildren().addAll(welcome1, welcome2, version, start, settings, exit);
        menuScene = new Scene(menu, 1000, 500);
    }

    void setSettingsScene() throws MidiUnavailableException, FileNotFoundException {
        Text choiceText = new Text(356, 190, "TYPE OF PIANO:");
        choiceText.setFill(gray);
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
                if (new_val.equals(programs.get(i))) {
                    piano1.changeProgram(0, i + 1);
                }
        });
        choiceBox.setLayoutX(424);
        choiceBox.setLayoutY(220);

        Text menu = new Text(10, 30, "MENU");
        menu.setFont(Font.loadFont(new FileInputStream(font), 24));
        menu.setFill(gray);
        menu.setOnMouseEntered(e -> menu.setFill(Color.WHITE));
        menu.setOnMouseExited(e -> menu.setFill(gray));
        menu.setOnMouseClicked(e -> primaryStage.setScene(menuScene));

        Text play = new Text(930, 30, "PLAY");
        play.setFont(Font.loadFont(new FileInputStream(font), 24));
        play.setFill(gray);
        play.setOnMouseEntered(e -> play.setFill(Color.WHITE));
        play.setOnMouseExited(e -> play.setFill(gray));
        play.setOnMouseClicked(e -> primaryStage.setScene(mainScene));

        Pane choice = new Pane();
        choice.setStyle("-fx-background-color: #959181");
        choice.getChildren().addAll(menu, choiceText, choiceBox, play);

        settingsScene = new Scene(choice, 1000, 500);
        primaryStage.setScene(menuScene);
    }

    void setMainScene() throws FileNotFoundException, MidiUnavailableException {
        Pane pane1 = new Pane();
        pane1.setStyle("-fx-background-color: #959181");

        Text backButton = new Text(10, 30, "BACK");
        backButton.setFill(gray);
        backButton.setFont(Font.loadFont(new FileInputStream(font), 24));
        backButton.setOnMouseEntered(e -> backButton.setFill(Color.WHITE));
        backButton.setOnMouseExited(e -> backButton.setFill(gray));
        backButton.setOnMouseClicked(e -> primaryStage.setScene(menuScene));

        Text currentOctaves = new Text(403, 130, "OCTAVE 4 - 5");
        currentOctaves.setFill(gray);
        currentOctaves.setFont(Font.loadFont(new FileInputStream(font), 30));

        ImageView previousOctave = new ImageView(new Image(new FileInputStream("src/resources/previous.png")));
        ImageView nextOctave = new ImageView(new Image(new FileInputStream("src/resources/next.png")));

        previousOctave.setOnMouseClicked(e -> {
            int currentOctave = piano1.getOctave();
            if (currentOctave > 1)
                piano1.setOctave(currentOctave - 1);
            try {
                changeText(currentOctaves, nextOctave, previousOctave);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        });
        previousOctave.setLayoutX(11);
        previousOctave.setLayoutY(103);

        nextOctave.setOnMouseClicked(e -> {
            int currentOctave = piano1.getOctave();
            if (currentOctave < 7)
                piano1.setOctave(currentOctave + 1);
            try {
                changeText(currentOctaves, nextOctave, previousOctave);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        });
        nextOctave.setLayoutX(932);
        nextOctave.setLayoutY(100);

        ImageView playButton = new ImageView(new Image(new FileInputStream("src/resources/play.png")));
        playButton.setOnMouseClicked(e -> piano1.playSong());
        playButton.setLayoutX(425);
        playButton.setLayoutY(11);

        ImageView recordButton = new ImageView(new Image(new FileInputStream("src/resources/record_inactive.png")));
        final boolean[] recordButtonStatus = {false};
        recordButton.setOnMouseClicked(e -> {
            if (!recordButtonStatus[0]) {
                try {
                    recordButton.setImage(new Image(new FileInputStream("src/resources/record_active.png")));
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                recordButtonStatus[0] = true;
                piano1.setStatus(true);
            } else {
                try {
                    recordButton.setImage(new Image(new FileInputStream("src/resources/record_inactive.png")));
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                recordButtonStatus[0] = false;
                piano1.setStatus(false);
            }
        });
        recordButton.setLayoutX(480);
        recordButton.setLayoutY(11);

        ImageView restartButton = new ImageView(new Image(new FileInputStream("src/resources/restart.png")));
        restartButton.setOnMouseClicked(e -> piano1.clear());
        restartButton.setLayoutX(535);
        restartButton.setLayoutY(11);

        ImageView settingsButton = new ImageView(new Image(new FileInputStream("src/resources/settings.png")));
        settingsButton.setOnMouseClicked(e -> primaryStage.setScene(settingsScene));
        settingsButton.setOnMouseEntered(e -> {
            try {
                settingsButton.setImage(
                        new Image(new FileInputStream("src/resources/settings_active.png"))
                );
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        });
        settingsButton.setOnMouseExited(e -> {
            try {
                settingsButton.setImage(
                        new Image(new FileInputStream("src/resources/settings.png"))
                );
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        });
        settingsButton.setLayoutX(909);
        settingsButton.setLayoutY(11);

        ImageView muteButton = new ImageView(new Image(new FileInputStream("src/resources/mute_unactive.png")));
        final boolean[] muteStatus = {false};
        muteButton.setOnMouseClicked(e -> {
            if (!muteStatus[0]) {
                muteStatus[0] = true;
                try {
                    muteButton.setImage(new Image(new FileInputStream("src/resources/mute_active.png")));
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                piano1.synthesizer.close();
                piano1.volume = 0;
            } else {
                muteStatus[0] = false;
                try {
                    muteButton.setImage(new Image(new FileInputStream("src/resources/mute_unactive.png")));
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                try {
                    piano1.synthesizer.open();
                } catch (MidiUnavailableException e1) {
                    e1.printStackTrace();
                }
                piano1.volume = 100;
            }
        });
        muteButton.setLayoutX(956);
        muteButton.setLayoutY(11);

        Text quote = new Text(104, 476, "“When you play, never mind who listens to you.” – Robert Schumann");
        quote.setFill(light);
        quote.setFont(Font.loadFont(new FileInputStream(font), 24));

        pane1.getChildren().addAll(
                piano1.getKeyPane(), backButton, muteButton, previousOctave, nextOctave,
                currentOctaves, recordButton, restartButton, playButton, settingsButton, quote
        );

        mainScene = new Scene(pane1, 1000, 500);
        piano1.addKeyEvent(mainScene);
    }

    private void changeText(Text currentOctaves, ImageView next, ImageView previous) throws FileNotFoundException {
        switch (piano1.getOctave()) {
            case 1:
                previous.setImage(new Image(new FileInputStream("src/resources/clear.png")));
                currentOctaves.setText("OCTAVE 1 - 2");
                break;
            case 2:
                previous.setImage(new Image(new FileInputStream("src/resources/previous.png")));
                currentOctaves.setText("OCTAVE 2 - 3");
                break;
            case 3:
                currentOctaves.setText("OCTAVE 3 - 4");
                break;
            case 4:
                currentOctaves.setText("OCTAVE 4 - 5");
                break;
            case 5:
                currentOctaves.setText("OCTAVE 5 - 6");
                break;
            case 6:
                next.setImage(new Image(new FileInputStream("src/resources/next.png")));
                currentOctaves.setText("OCTAVE 6 - 7");
                break;
            case 7:
                next.setImage(new Image(new FileInputStream("src/resources/clear.png")));
                currentOctaves.setText("OCTAVE 7 - 8");
                break;
        }
    }
}
