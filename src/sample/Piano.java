package sample;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import java.io.FileNotFoundException;
import java.util.*;

class Piano {
    private List<Key> keys = new ArrayList<>();
    private Map<KeyCode, Integer> keyboard = new HashMap<>();
    Synthesizer synthesizer;
    int volume = 100;
    private int octave = 4;
    private MidiChannel[] channels = null;

    private List<Note> notes = new ArrayList<>();
    private List<Date> dates = new ArrayList<>();

    private boolean isRecording = false;

    Piano(int locationX, int locationY) throws FileNotFoundException, MidiUnavailableException {
        for (int i = 0; i < 24; i++) {
            Key key = new Key(i);
            keys.add(i, key);

            switch (key.getType()) {
                case WHITE:
                    key.setKey(locationX, locationY);
                    locationX += 70;
                    break;
                case BLACK:
                    locationX -= 25;
                    key.setKey(locationX, locationY);
                    locationX += 25;
                    break;
            }
        }

        synthesizer = MidiSystem.getSynthesizer();
        synthesizer.open();
        channels = synthesizer.getChannels();
    }

    void addKeyEvent(Scene scene) {
        keyboard.put(KeyCode.Z, 0);
        keyboard.put(KeyCode.W, 1);
        keyboard.put(KeyCode.S, 2);
        keyboard.put(KeyCode.E, 3);
        keyboard.put(KeyCode.X, 4);
        keyboard.put(KeyCode.C, 5);
        keyboard.put(KeyCode.R, 6);
        keyboard.put(KeyCode.F, 7);
        keyboard.put(KeyCode.T, 8);
        keyboard.put(KeyCode.V, 9);
        keyboard.put(KeyCode.Y, 10);
        keyboard.put(KeyCode.B, 11);
        keyboard.put(KeyCode.H, 12);
        keyboard.put(KeyCode.U, 13);
        keyboard.put(KeyCode.N, 14);
        keyboard.put(KeyCode.I, 15);
        keyboard.put(KeyCode.M, 16);
        keyboard.put(KeyCode.K, 17);
        keyboard.put(KeyCode.O, 18);
        keyboard.put(KeyCode.COMMA, 19);
        keyboard.put(KeyCode.P, 20);
        keyboard.put(KeyCode.PERIOD, 21);
        keyboard.put(KeyCode.OPEN_BRACKET, 22);
        keyboard.put(KeyCode.SEMICOLON, 23);

        for (int i = 0; i < 24; i++) {
            Key key = keys.get(i);
            key.getImageView().setOnMousePressed(e -> {
                key.changeStatus(true);
                key.changeImage();
                Note note = new Note(octave, key.getId(), volume);
                playSound(note, 0);
                if (isRecording) {
                    notes.add(notes.size(), note);
                    dates.add(dates.size(), new Date());
                }
            });

            key.getImageView().setOnMouseReleased(e -> {
                key.changeStatus(false);
                key.changeImage();
            });
        }

        scene.setOnKeyPressed(e -> {
            if (keyboard.containsKey(e.getCode()) && !keys.get(keyboard.get(e.getCode())).getStatus()) {
                int code = keyboard.get(e.getCode());
                Key thisKey = keys.get(code);
                thisKey.changeStatus(true);
                thisKey.changeImage();
                Note note = new Note(octave, code, volume);
                playSound(note, 0);

                if (isRecording) {
                    notes.add(notes.size(), note);
                    dates.add(dates.size(), new Date());
                }
            }
        });

        scene.setOnKeyReleased(e -> {
            if (keyboard.containsKey(e.getCode())) {
                int code = keyboard.get(e.getCode());
                Key thisKey = keys.get(code);
                thisKey.changeStatus(false);
                thisKey.changeImage();
            }
        });
    }

    Pane getKeyPane() {
        Pane pane = new Pane();
        for (Key key : keys)
            pane.getChildren().add(key.getImageView());
        return pane;
    }

    private void playSound(Note note, int channel) {
        int firstNote = 12 * (note.getOctave() + 1);
        channels[channel].noteOn(note.getNum() + firstNote, note.getVolume());
    }

    void setOctave(int oct) {
        octave = oct;
    }

    int getOctave() {
        return octave;
    }

    void changeProgram(int ch, int num) {
        channels[ch].programChange(num);
    }

    void playSong() {
        JThread thread = new JThread(notes);
        thread.start();
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

    void clear() {
        notes.clear();
    }

    void setStatus(boolean status) {
        this.isRecording = status;
    }
}
