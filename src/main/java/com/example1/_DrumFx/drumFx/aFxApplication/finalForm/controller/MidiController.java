package com.example1._DrumFx.drumFx.aFxApplication.finalForm.controller;

import com.example1._DrumFx.drumFx.aFxApplication.finalForm.model.PianoConstants;
import lombok.Getter;
import lombok.Setter;

import javax.sound.midi.*;

@Getter
@Setter
public class MidiController {

    private int tempo = 120;
    private Sequencer sequencer;
    private Sequence sequence;
    private Track track;

    private int[] instruments;

    //Different drum presets
    private final int[][] presets = {
            {35, 42, 46, 38, 49, 39, 50, 47},
            {36, 44, 48, 40, 51, 41, 52, 45},
            {37, 43, 47, 39, 50, 42, 53, 46},
            {35, 42, 44, 46, 38, 49, 39, 51},
            {36, 44, 48, 40, 51, 41, 52, 57}
    };

    private RecordingController recordingController;

    private static final int RESOLUTION = 8; // Ticks per quarter note

    public MidiController(Sequencer sequencer, Sequence sequence, RecordingController recordingController) {
        this.sequencer = sequencer;
        this.sequence = sequence;
        this.recordingController = recordingController;
        setPreset(0);
        setUpMidi();
    }

    //change preset
    public void setPreset(int presetIndex) {
        if (presetIndex >= 0 && presetIndex < presets.length) {
            instruments = presets[presetIndex];
        }
    }

// Deletes the current track and creates a new one.
// Sets the sequencer tempo and adds a tempo change event.
    private void setUpMidi() {
        try {
            sequence.deleteTrack(track);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(tempo);
            addTempoChangeEvent(tempo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


// Builds a MIDI track based on the state of checkboxes.
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
        track.add(makeEvent(ShortMessage.PROGRAM_CHANGE, 9, 1, 0, 16 * PianoConstants.RESOLUTION));

        try {
            sequencer.setSequence(sequence);
            sequencer.setLoopStartPoint(0);
            sequencer.setLoopEndPoint(16 * PianoConstants.RESOLUTION - 1);
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

    //Creates MIDI events for the given list of notes, adds note on and note off events to the track
    private void makeTrack(int[] list) {
        for (int i = 0; i < list.length; i++) {
            int key = list[i];
            if (key != 0) {
                int tick = i * RESOLUTION;
                MidiEvent onEvent = makeEvent(ShortMessage.NOTE_ON, 9, key, 100, tick);
                MidiEvent offEvent = makeEvent(ShortMessage.NOTE_OFF, 9, key, 100, tick + RESOLUTION - 1);
                track.add(onEvent);
                track.add(offEvent);
                recordingController.addMidiEvent(onEvent);
                recordingController.addMidiEvent(offEvent);
            }
        }
    }

    //Creates a midi event
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
            MidiEvent tempoEvent = new MidiEvent(tempoMessage, 0);
            sequence.getTracks()[0].add(tempoEvent); // Ensure it is added to the first track
            recordingController.addMidiEvent(tempoEvent); // Record tempo change event
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    public void addCurrentTempoToTrack() {
        addTempoChangeEvent((int) sequencer.getTempoInBPM());
    }
}

