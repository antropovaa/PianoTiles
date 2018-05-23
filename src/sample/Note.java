package sample;

class Note {
    private int octave, num, volume;

    Note(int octave, int num, int volume) {
        this.octave = octave;
        this.num = num;
        this.volume = volume;
    }

    int getNum() {
        return num;
    }

    int getOctave() {
        return octave;
    }

    int getVolume() {
        return volume;
    }
}
