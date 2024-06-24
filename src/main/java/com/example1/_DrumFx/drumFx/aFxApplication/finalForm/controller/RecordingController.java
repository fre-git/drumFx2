package com.example1._DrumFx.drumFx.aFxApplication.finalForm.controller;

import lombok.Getter;
import lombok.Setter;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;

@Getter
@Setter
public class RecordingController {
    private Sequencer sequencer;
    private Sequence recordingSequence;
    private Track recordingTrack;
    private File recordingFile;
    private boolean isRecording;
    private long tickOffset = 0;

    public RecordingController(Sequencer sequencer) {
        this.sequencer = sequencer;
        setUpRecording();
    }

    private void setUpRecording() {
        try {
            recordingSequence = new Sequence(Sequence.PPQ, 24);
            recordingTrack = recordingSequence.createTrack();
            isRecording = false;
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    public void startRecording(File file) {
        recordingFile = file;
        isRecording = true;
        tickOffset = 0; // Initialize tick offset
        recordingTrack = recordingSequence.createTrack(); // Start a new recording track
    }

    private void saveRecording() {
        try {
            MidiSystem.write(recordingSequence, 1, recordingFile);
            System.out.println("Recording saved to " + recordingFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        isRecording = false;
        saveRecording();
        setUpRecording();
    }

    public void addMidiEvent(MidiEvent event) {
        if (isRecording) {
            long newTick = event.getTick() + tickOffset;
            MidiEvent newEvent = new MidiEvent(event.getMessage(), newTick);
            recordingTrack.add(newEvent);
        }
    }

    public void updateTickOffset() {
        tickOffset += sequencer.getTickLength();
    }
}
