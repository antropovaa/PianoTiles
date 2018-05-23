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
        for (int i = 0; i < 12; i++) {
            Key key = new Key(i);
            keys.add(i, key);

            switch (i) {
                case 0:
                case 2:
                case 4:
                case 5:
                case 7:
                case 9:
                case 11:
                    key.setKey(locationX, locationY);
                    locationX += 90;
                    break;
                default:
                    locationX -= 30;
                    key.setKey(locationX, locationY);
                    locationX += 30;
                    break;
            }
        }

        synthesizer = MidiSystem.getSynthesizer();
        synthesizer.open();
        channels = synthesizer.getChannels();
    }

    void addKeyEvent(Scene scene) {
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

        for (int i = 0; i < 12; i++) {
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
                int code = keyboard.get(e.getCode());
                Key thisKey = keys.get(code);
                if (keyboard.containsKey(e.getCode()) && !thisKey.getStatus()) {
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
                int code = keyboard.get(e.getCode());
                Key thisKey = keys.get(code);
                if (keyboard.containsKey(e.getCode())) {
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
