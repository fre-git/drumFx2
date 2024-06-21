package com.example1._DrumFx.drumFx.fxApplication.finalEstForm.controller;

import lombok.Getter;
import lombok.Setter;

import javax.sound.midi.*;

import static javax.sound.midi.ShortMessage.*;

@Getter
@Setter
public class MidiManager {
    private int tempo = 120;
    private Sequencer sequencer;
    private Sequence sequence;
    private Track track;

    private int[] instruments;

    private final int[][] presets = {
            {35, 42, 46, 38, 49, 39, 50, 47}, // Preset 1
            {36, 44, 48, 40, 51, 41, 52, 45}, // Preset 2
            {37, 43, 47, 39, 50, 42, 53, 46}, // Preset 3
            {35, 42, 44, 46, 38, 49, 39, 51}, // Preset 4
            {36, 44, 48, 40, 51, 41, 52, 57}  // Preset 5
    };

    private static final int RESOLUTION = 8; // Ticks per quarter note

    public MidiManager(Sequencer sequencer, Sequence sequence) {
        this.sequencer = sequencer;
        this.sequence = sequence;
        setPreset(0); // Set default preset
        setUpMidi();
    }

    public void setPreset(int presetIndex) {
        if (presetIndex >= 0 && presetIndex < presets.length) {
            instruments = presets[presetIndex];
        }
    }

    private void setUpMidi() {
        try {
            sequence.deleteTrack(track);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(tempo);
            addTempoChangeEvent(tempo); // Add initial tempo change event
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buildTrack(boolean[] checkboxState) {
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
        track.add(makeEvent(ShortMessage.PROGRAM_CHANGE, 9, 1, 0, 16 * RESOLUTION));

        try {
            sequencer.setSequence(sequence);
            sequencer.setLoopStartPoint(0);
            sequencer.setLoopEndPoint(16 * RESOLUTION - 1); // Correct loop endpoint
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    public void startSequencer() {
        if (!sequencer.isRunning()) {
            sequencer.start();
        }
    }

    private void makeTrack(int[] list) {
        for (int i = 0; i < list.length; i++) {
            int key = list[i];
            if (key != 0) {
                int tick = i * RESOLUTION; // Ensure correct spacing between notes
                track.add(makeEvent(ShortMessage.NOTE_ON, 9, key, 100, tick));
                track.add(makeEvent(ShortMessage.NOTE_OFF, 9, key, 100, tick + RESOLUTION - 1));
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

    public void changeTempo(int tempoChange) {
        tempo += tempoChange;
//        if (tempo < 20) {
//            tempo = 20;  // Minimum tempo
//        } else if (tempo > 300) {
//            tempo = 300;  // Maximum tempo
//        }
        sequencer.setTempoInBPM(tempo);
    }

    public void setTempo(int bpm) {
        tempo = bpm;
        sequencer.setTempoInBPM(bpm);
        addTempoChangeEvent(bpm);
    }

    private void addTempoChangeEvent(int bpm) {
        try {
            int mpq = 60000000 / bpm; // microseconds per quarter note
            byte[] data = {(byte) (mpq >> 16), (byte) (mpq >> 8), (byte) mpq};
            MetaMessage tempoMessage = new MetaMessage();
            tempoMessage.setMessage(0x51, data, data.length);
            sequence.getTracks()[0].add(new MidiEvent(tempoMessage, 0)); // Ensure it is added to the first track
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    public void addCurrentTempoToTrack() {
        addTempoChangeEvent((int) sequencer.getTempoInBPM());
    }
}

