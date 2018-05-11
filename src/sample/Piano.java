package sample;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

class Piano {
    private List<ImageView> keys = new ArrayList<>();
    private List<Image> keysUnchecked = new ArrayList<>();
    private List<Image> keysClicked = new ArrayList<>();
    int volume = 100;
    private int octave = 4;
    private MidiChannel[] channels = null;
    Synthesizer synth;

    Piano() throws FileNotFoundException, MidiUnavailableException {
        String path = "/Users/annaantropova/IdeaProjects/PianoTiles/src/images/";
        List<FileInputStream> inputsClicked = new ArrayList<>();
        List<FileInputStream> inputs = new ArrayList<>();

        // creating keys ImageView
        for (int i = 0; i < 12; i++) {
            String fileName1;
            String fileName2;
            String clicked = "_clicked";
            switch (i) {
                case 0:
                case 5:
                    fileName1 = "left";
                    fileName2 = fileName1 + clicked;
                    break;
                case 2:
                case 7:
                case 9:
                    fileName1 = "middle";
                    fileName2 = fileName1 + clicked;
                    break;
                case 4:
                case 11:
                    fileName1 = "right";
                    fileName2 = fileName1 + clicked;
                    break;
                default:
                    fileName1 = "black";
                    fileName2 = fileName1 + clicked;
                    break;
            }
            inputs.add(i, new FileInputStream(path + fileName1 + ".png"));
            inputsClicked.add(i, new FileInputStream(path + fileName2 + ".png"));

            Image newImage = new Image(inputs.get(i));
            keysUnchecked.add(i, newImage);
            newImage = new Image(inputsClicked.get(i));
            keysClicked.add(i, newImage);
        }

        // locating keys on the scene as ImageView
        int locationY = 150;
        int locationX = 85;
        for (int i = 0; i < 12; i++) {
            switch (i) {
                case 0:
                case 2:
                case 4:
                case 5:
                case 7:
                case 9:
                case 11:
                    ImageView white = new ImageView(keysUnchecked.get(i));
                    setKey(white, i, locationX, locationY);
                    locationX += 90;
                    break;
                default:
                    locationX -= 30;
                    ImageView black = new ImageView(keysUnchecked.get(i));
                    setKey(black, i, locationX, locationY);
                    locationX += 30;
                    break;
            }
        }

        // creating synthesizer
        synth = MidiSystem.getSynthesizer();
        synth.open();
        channels = synth.getChannels();

        // making keys play sounds
        for (int i = 0; i < 12; i++) {
            int num = i;
            getKey(num).setOnMousePressed(e -> {
                changeKeyImage(num);
                playSound(octave, num, volume);
            });
            getKey(num).setOnMouseReleased(e -> returnKeyImage(num));
        }
    }

    private ImageView getKey(int n) {
        return keys.get(n);
    }

    private void changeKeyImage(int n) {
        getKey(n).setImage(keysClicked.get(n));
    }

    private void returnKeyImage(int n) {
        getKey(n).setImage(keysUnchecked.get(n));
    }

    Pane getKeyPane() {
        Pane pane = new Pane();
        for (ImageView key : keys)
            pane.getChildren().add(key);
        return pane;
    }

    void setKey(ImageView key, int num, int x, int y) {
        keys.add(num, key);
        keys.get(num).setLayoutX(x);
        keys.get(num).setLayoutY(y);
    }

    private void playSound(int octave, int key, int volume) {
        int firstNote = 12 * (octave + 1);
        channels[0].noteOn(key + firstNote, volume);
    }

    void setOctave(int oct) {
        octave = oct;
    }

    void changeProgram(int n) {
        channels[0].programChange(n);
    }

}
