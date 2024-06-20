package com.example1._DrumFx.drumFx.fxApplication.FinalForm;

import javax.sound.midi.*;

import static javax.sound.midi.ShortMessage.*;

public class MidiManager {

    private int tempo = 120;
    private Sequencer sequencer;
    private Sequence sequence;
    private Track track;
    private final int[] instruments = {35, 42, 46, 38, 49, 39, 50, 47};
    private static final int RESOLUTION = 24; // Ticks per quarter note

    public MidiManager(Sequencer sequencer, Sequence sequence) {
        this.sequencer = sequencer;
        this.sequence = sequence;
        setUpMidi();
    }

    private void setUpMidi() {
        try {
            track = sequence.createTrack();
            sequencer.setTempoInBPM(tempo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buildTrackAndStart(boolean[] checkboxState) {
        int[] trackList;

        sequence.deleteTrack(track);
        track = sequence.createTrack();

        for (int i = 0; i < 8; i++) {
            trackList = new int[16];
            int key = instruments[i];

            for (int j = 0; j < 16; j++) {
                trackList[j] = checkboxState[j + 16 * i] ? key : 0;
            }

            makeTrack(trackList);
        }

        // Ensure the loop covers the entire measure
        track.add(makeEvent(PROGRAM_CHANGE, 9, 1, 0, 16 * RESOLUTION));

        try {
            sequencer.setSequence(sequence);
            sequencer.setLoopStartPoint(0);
            sequencer.setLoopEndPoint(16 * RESOLUTION - 1); // Correct loop endpoint
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            sequencer.setTempoInBPM(tempo);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    private void makeTrack(int[] list) {
        for (int i = 0; i < list.length; i++) {
            int key = list[i];
            if (key != 0) {
                int tick = i * RESOLUTION; // Ensure correct spacing between notes
                track.add(makeEvent(NOTE_ON, 9, key, 100, tick));
                track.add(makeEvent(NOTE_OFF, 9, key, 100, tick + RESOLUTION - 1));
            }
        }
    }

    private MidiEvent makeEvent(int cmd, int chnl, int one, int two, int tick) {
        MidiEvent event = null;
        try {
            ShortMessage msg = new ShortMessage();
            msg.setMessage(cmd, chnl, one, two);
            event = new MidiEvent(msg, tick);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
        return event;
    }

    public void stop() {
        if (sequencer.isRunning()) {
            sequencer.stop();
        }
        sequencer.setTickPosition(0);
    }

    public void changeTempo(float tempoMultiplier) {
        tempo = Math.round(tempo * tempoMultiplier);
        sequencer.setTempoInBPM(tempo);
    }

    public void setTempo(int bpm) {
        tempo = bpm;
        sequencer.setTempoInBPM(tempo);
    }
}
