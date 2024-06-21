package com.example1._DrumFx.drumFx.fxApplication.finalEstForm.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import javax.sound.midi.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PianoRoll {


    private int tempo = 120;
    private Sequencer sequencer;
    private Sequence sequence;
    private Track track;
    private MidiChannel midiChannel;
    private int currentInstrument = 0;

    private final int[] instrumentPresets = {
            0, 1, 2, 3, 4, 5, 6, 7,    // Piano
            32, 33, 34, 35, 36,        // Bass
            64, 65, 66, 67, 68         // Synth
    };

    public PianoRoll(Sequencer sequencer, Sequence sequence) {
        this.sequencer = sequencer;
        this.sequence = sequence;
        initializeMIDI();
    }

    private void initializeMIDI() {
        try {
            sequence.deleteTrack(track);
            track = sequence.createTrack();
            Synthesizer synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            midiChannel = synthesizer.getChannels()[0];
            midiChannel.programChange(currentInstrument);
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void setInstrument(int instrument) {
        if (instrument >= 0 && instrument < 128) {
            currentInstrument = instrument;
            midiChannel.programChange(currentInstrument);
        }
    }

//    public void changeTempo(int changeTempo) {
//        tempo = Math.round(tempo * tempoMultiplier);
//        sequencer.setTempoInBPM(tempo);
//    }

    public void changeTempo(int tempoChange) {
        tempo += tempoChange;
        sequencer.setTempoInBPM(tempo);
    }

    public void setTempo(int bpm) {
        sequencer.setTempoInBPM(bpm);
        addTempoChangeEvent(bpm);
    }

    private void addTempoChangeEvent(int bpm) {
        try {
            int mpq = 60000000 / bpm;
            byte[] data = {(byte) (mpq >> 16), (byte) (mpq >> 8), (byte) mpq};
            MetaMessage tempoMessage = new MetaMessage();
            tempoMessage.setMessage(0x51, data, data.length);
            sequence.getTracks()[0].add(new MidiEvent(tempoMessage, 0));
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

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

    private MidiEvent makeEvent(int command, int channel, int data1, int data2, long tick) {
        MidiEvent event = null;
        try {
            ShortMessage message = new ShortMessage();
            message.setMessage(command, channel, data1, data2);
            event = new MidiEvent(message, tick);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
        return event;
    }











//    private List<Note> notes;
//    private Sequencer sequencer;
//    private Sequence sequence;
//    private Track track;
//    private MidiChannel midiChannel;
//    private int currentInstrument = 0;
//    private ObjectMapper objectMapper;
//
//    private static final int RESOLUTION = 8;
//    private static final int MIN_MIDI_PITCH = 21;
//    private static final int KEY_HEIGHT = 15;
//    private static final int NOTE_WIDTH = 55;
//    private static final int BEATS_PER_MEASURE = 4;
//
//    private int tempo = 120; // Store the current tempo
//
//    private final int[] instrumentPresets = {
//            0, 1, 2, 3, 4, 5, 6, 7,
//            32, 33, 34, 35, 36,
//            64, 65, 66, 67, 68
//    };
//
//    public PianoRoll(Sequencer sequencer, Sequence sequence) {
//        this.sequencer = sequencer;
//        this.sequence = sequence;
//        this.notes = new ArrayList<>();
//        this.objectMapper = new ObjectMapper();
//        initializeMIDI();
//    }
//
//    private void initializeMIDI() {
//        try {
//            sequence.deleteTrack(track);
//            track = sequence.createTrack();
//            Synthesizer synthesizer = MidiSystem.getSynthesizer();
//            synthesizer.open();
//            midiChannel = synthesizer.getChannels()[0];
//            midiChannel.programChange(currentInstrument);
//        } catch (MidiUnavailableException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void setInstrument(int instrument) {
//        if (instrument >= 0 && instrument < 128) {
//            currentInstrument = instrument;
//            midiChannel.programChange(currentInstrument);
//        }
//    }
//
//    public void addNoteAt(double x, double y) {
//        double snappedX = Math.floor(x / NOTE_WIDTH) * NOTE_WIDTH;
//        double snappedY = Math.floor(y / KEY_HEIGHT) * KEY_HEIGHT;
//        Note note = new Note(snappedX, snappedY, NOTE_WIDTH, KEY_HEIGHT);
//        int pitch = (int) ((1720 - note.getY()) / KEY_HEIGHT) + MIN_MIDI_PITCH;
//        playNoteImmediately(pitch);
//        notes.add(note);
//        updateTrack();
//    }
//
//    private void playNoteImmediately(int pitch) {
//        new Thread(() -> {
//            try {
//                midiChannel.noteOn(pitch, 64);
//                Thread.sleep(500);
//                midiChannel.noteOff(pitch);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }).start();
//    }
//
//    private void addMIDINoteEvent(int command, int channel, int pitch, int velocity, long tick) {
//        try {
//            ShortMessage message = new ShortMessage();
//            message.setMessage(command, channel, pitch, velocity);
//            MidiEvent event = new MidiEvent(message, tick);
//            track.add(event);
//        } catch (InvalidMidiDataException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void addNoteToTrack(Note note) {
//        int pitch = (int) ((1720 - note.getY()) / KEY_HEIGHT) + MIN_MIDI_PITCH;
//        int startTime = (int) (note.getX() / NOTE_WIDTH);
//        int duration = (int) (note.getWidth() / NOTE_WIDTH);
//        addMIDINoteEvent(ShortMessage.NOTE_ON, 0, pitch, 64, (long) startTime * RESOLUTION);
//        addMIDINoteEvent(ShortMessage.NOTE_OFF, 0, pitch, 64, (long) (startTime + duration) * RESOLUTION);
//    }
//
//    public void updateTrack() {
//        sequence.deleteTrack(track);
//        track = sequence.createTrack();
//        for (Note note : notes) {
//            addNoteToTrack(note);
//        }
//    }
//
//    public void play() {
//        try {
//            sequencer.setSequence(sequence);
//            sequencer.start();
//        } catch (InvalidMidiDataException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void stop() {
//        if (sequencer.isRunning()) {
//            sequencer.stop();
//        }
//        sequencer.setTickPosition(0);
//    }
//
//    public void changeTempo(int tempoChange) {
//        tempo += tempoChange;
//        if (tempo < 20) {
//            tempo = 20;  // Minimum tempo
//        } else if (tempo > 300) {
//            tempo = 300;  // Maximum tempo
//        }
//        sequencer.setTempoInBPM(tempo);
//    }
//
//    public void setTempo(int bpm) {
//        tempo = bpm;
//        sequencer.setTempoInBPM(bpm);
//        addTempoChangeEvent(bpm);
//    }
//
//    private void addTempoChangeEvent(int bpm) {
//        try {
//            int mpq = 60000000 / bpm;
//            byte[] data = {(byte) (mpq >> 16), (byte) (mpq >> 8), (byte) mpq};
//            MetaMessage tempoMessage = new MetaMessage();
//            tempoMessage.setMessage(0x51, data, data.length);
//            sequence.getTracks()[0].add(new MidiEvent(tempoMessage, 0));
//        } catch (InvalidMidiDataException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public String saveState() {
//        try {
//            return objectMapper.writeValueAsString(notes);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    public void loadState(String state) {
//        try {
//            List<Note> loadedNotes = objectMapper.readValue(state, new TypeReference<List<Note>>() {});
//            this.notes.clear();
//            this.notes.addAll(loadedNotes);
//            updateTrack();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public double calculateDuration() {
//        double maxEndTime = 0;
//        for (Note note : notes) {
//            double noteEndTime = note.getX() + note.getWidth();
//            if (noteEndTime > maxEndTime) {
//                maxEndTime = noteEndTime;
//            }
//        }
//        return maxEndTime / (NOTE_WIDTH * 4);
//    }
//
//    public void removeNoteAt(double x, double y) {
//        Note clickedNote = getNoteAt(x, y);
//        if (clickedNote != null) {
//            notes.remove(clickedNote);
//            updateTrack();
//        }
//    }
//
//    public Note getNoteAt(double x, double y) {
//        for (Note note : notes) {
//            if (x >= note.getX() && x <= note.getX() + note.getWidth() && y >= note.getY() && y <= note.getY() + note.getHeight()) {
//                return note;
//            }
//        }
//        return null;
//    }
//
//    public void updateTrackLength(int measures) {
//        long ticks = measures * BEATS_PER_MEASURE * RESOLUTION;
//        if (ticks <= 0) {
//            throw new IllegalArgumentException("Invalid number of measures or ticks calculation.");
//        }
//        track.add(makeEvent(ShortMessage.PROGRAM_CHANGE, 9, 1, 0, ticks - 1));
//        try {
//            sequencer.setSequence(sequence);
//            sequencer.setLoopStartPoint(0);
//            sequencer.setLoopEndPoint(ticks - 1);
//        } catch (InvalidMidiDataException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private MidiEvent makeEvent(int command, int channel, int data1, int data2, long tick) {
//        MidiEvent event = null;
//        try {
//            ShortMessage message = new ShortMessage();
//            message.setMessage(command, channel, data1, data2);
//            event = new MidiEvent(message, tick);
//        } catch (InvalidMidiDataException e) {
//            e.printStackTrace();
//        }
//        return event;
//    }
}