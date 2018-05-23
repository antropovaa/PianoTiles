package sample;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class Piano {
    private List<ImageView> keys = new ArrayList<>();
    private List<Image> keysUnchecked = new ArrayList<>();
    private List<Image> keysClicked = new ArrayList<>();
    int volume = 100;
    private int octave = 4;
    private MidiChannel[] channels = null;
    Synthesizer synth;
    private List<Note> notes = new ArrayList<>();
    private List<Date> dates = new ArrayList<>();
    private boolean recordingStatus = false;

    public Piano() throws FileNotFoundException, MidiUnavailableException {
        String path = "src/resources/";
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

        // making keys play sounds by mouse events
        addMouseEvent();
    }

    void addKeyEvent(Scene mainScene) {
        boolean[] isKeyPressed = new boolean[12];
        for (int i = 1; i < 12; i++)
            isKeyPressed[i] = false;

        Map<KeyCode, Integer> keyboard = new HashMap<>();
        keyboard.put(KeyCode.D, 0);
        keyboard.put(KeyCode.R, 1);
        keyboard.put(KeyCode.F, 2);
        keyboard.put(KeyCode.T, 3);
        keyboard.put(KeyCode.G, 4);
        keyboard.put(KeyCode.H, 5);
        keyboard.put(KeyCode.U, 6);
        keyboard.put(KeyCode.J, 7);
        keyboard.put(KeyCode.I, 8);
        keyboard.put(KeyCode.K, 9);
        keyboard.put(KeyCode.O, 10);
        keyboard.put(KeyCode.L, 11);

        mainScene.setOnKeyPressed(e -> {
            int num = keyboard.get(e.getCode());
            if (keyboard.containsKey(e.getCode()) && !isKeyPressed[num]) {
                changeKeyImage(num);
                Note note = new Note(octave, num, volume);
                playSound(note, 0);

                if (isRecording()) {
                    //recorder.addNote(note);
                    notes.add(notes.size(), note);
                    dates.add(dates.size(), new Date());
                    isKeyPressed[num] = true;
                }
            }
        });

        mainScene.setOnKeyReleased(e -> {
            int num = keyboard.get(e.getCode());
            if (keyboard.containsKey(e.getCode())) {
                isKeyPressed[num] = false;
                returnKeyImage(num);
            }
        });
    }

    void addMouseEvent() {
        for (int i = 0; i < 12; i++) {
            int num = i;
            getKey(num).setOnMousePressed(e -> {
                changeKeyImage(num);
                Note note = new Note(octave, num, volume);
                playSound(note, 0);
                if (isRecording()) {
                    notes.add(notes.size(), note);
                    dates.add(dates.size(), new Date());
                }
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

    private void setKey(ImageView key, int num, int x, int y) {
        keys.add(num, key);
        keys.get(num).setLayoutX(x);
        keys.get(num).setLayoutY(y);
    }

    public void playSound(Note note, int channel) {
        int firstNote = 12 * (note.getOctave() + 1);
        channels[channel].noteOn(note.getNum() + firstNote, note.getVolume());
    }

    void setOctave(int oct) {
        octave = oct;
    }

    void changeProgram(int ch, int num) {
        channels[ch].programChange(num);
    }

    class JThread extends Thread {
        List<Note> notes;

        JThread(List<Note> notes) {
            this.notes = notes;
        }

        public void run() {
            changeProgram(1, channels[0].getProgram());
            if (notes.size() != 0) {
                for (int i = 0; i < notes.size() - 1; i++) {
                    playSound(notes.get(i), 1);
                    long time = dates.get(i + 1).getTime() - dates.get(i).getTime();
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                playSound(notes.get(notes.size() - 1), 1);
            }
        }
    }

    void playSong() {
        JThread thread = new JThread(notes);
        thread.start();
    }

    void clear() {
        notes.clear();
    }

    private boolean isRecording() {
        return this.recordingStatus;
    }

    void setStatus(boolean status) {
        this.recordingStatus = status;
    }

}
