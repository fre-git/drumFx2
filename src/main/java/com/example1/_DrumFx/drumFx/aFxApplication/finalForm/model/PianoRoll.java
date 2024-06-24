package com.example1._DrumFx.drumFx.aFxApplication.finalForm.model;

import com.example1._DrumFx.drumFx.aFxApplication.finalForm.controller.RecordingController;
import lombok.Getter;
import lombok.Setter;

import javax.sound.midi.*;

@Getter
@Setter
public class PianoRoll {
    private int tempo = 120;
    private Sequencer sequencer;
    private Sequence sequence;
    private Track track;
    private MidiChannel midiChannel;
    private int currentInstrument = 0;

    //Synth presets
    private final int[] instrumentPresets = {
            0, 1, 2, 3, 4, 5, 6, 7,    // Piano
            32, 33, 34, 35, 36,        // Bass
            64, 65, 66, 67, 68         // Synth
    };

    private RecordingController recordingController;

    public PianoRoll(Sequencer sequencer, Sequence sequence, RecordingController recordingController) {
        this.sequencer = sequencer;
        this.sequence = sequence;
        this.recordingController = recordingController;
        initializeMIDI();
    }

    // initialize the synthesizer and MIDI channel
    private void initializeMIDI() {
        try {
            sequence.deleteTrack(track);
            track = sequence.createTrack();
            Synthesizer synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            midiChannel = synthesizer.getChannels()[0];
            midiChannel.programChange(currentInstrument);
            addProgramChangeEvent(currentInstrument, 0);
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    // sets the instrument to be used for playback
    public void setInstrument(int instrument) {
        if (instrument >= 0 && instrument < 128) {
            currentInstrument = instrument;
            midiChannel.programChange(currentInstrument);
            addProgramChangeEvent(currentInstrument, sequencer.getTickPosition());
        }
    }

    public void changeTempo(int tempoChange) {
        tempo += tempoChange;
        sequencer.setTempoInBPM(tempo);
    }


    //adds an instrument change to the midi track
    private void addProgramChangeEvent(int instrument, long tick) {
        try {
            ShortMessage programChange = new ShortMessage();
            programChange.setMessage(ShortMessage.PROGRAM_CHANGE, 0, instrument, 0);
            MidiEvent event = new MidiEvent(programChange, tick);
            track.add(event);
            recordingController.addMidiEvent(event);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    // sets the loop start and end point based on the number of measures
    public void updateTrackLength(int measures) {
        long ticks = measures * 4 * 8;
        if (ticks <= 0) {
            throw new IllegalArgumentException("Invalid number of measures or ticks calculation.");
        }
        track.add(makeEvent(ShortMessage.PROGRAM_CHANGE, 9, 1, 0, ticks - 1));
        try {
            sequencer.setSequence(sequence);
            sequencer.setLoopStartPoint(0);
            sequencer.setLoopEndPoint(ticks - 1);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }


    // creates a midi event
    private MidiEvent makeEvent(int command, int channel, int data1, int data2, long tick) {
        MidiEvent event = null;
        try {
            ShortMessage message = new ShortMessage();
            message.setMessage(command, channel, data1, data2);
            event = new MidiEvent(message, tick);
            track.add(event);
            recordingController.addMidiEvent(event); // Add event to recording
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
        return event;
    }
}