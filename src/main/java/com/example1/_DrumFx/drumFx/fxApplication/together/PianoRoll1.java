package com.example1._DrumFx.drumFx.fxApplication.together;

import com.example1._DrumFx.drumFx.fxApplication.pianoRollNew.Note;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.List;

public class PianoRoll1 extends Canvas {
    private static final int KEY_HEIGHT = 10;
    private static final int NOTE_WIDTH = 10; // Reduced width for more lines
    private static final int BEATS_PER_MEASURE = 8;
    private static final int MEASURE_WIDTH = NOTE_WIDTH * BEATS_PER_MEASURE; // Width of a measure
    private static final int RESIZE_MARGIN = 5;
    private static final int MIN_MIDI_PITCH = 21; // A0 MIDI pitch
    private static final int MAX_MIDI_PITCH = 108; // C8 MIDI pitch
    private static final int NUM_MEASURES = 8; // Number of measures to display

    private List<Note> notes;
    private Note selectedNote = null;
    private boolean resizing = false;

    private Sequencer sequencer;
    private Sequence sequence;
    private Track track;
    private MidiChannel midiChannel;

    public PianoRoll1(Sequencer sequencer, Sequence sequence) {
        super(MEASURE_WIDTH * 16, 880);
        this.sequencer = sequencer;
        this.sequence = sequence;
        notes = new ArrayList<>();
        drawGrid();

        // Initialize MIDI components
        initializeMIDI();

        // Add event handlers
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleMouseClick);
        this.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePress);
        this.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handleMouseDrag);
        this.addEventHandler(MouseEvent.MOUSE_RELEASED, this::handleMouseRelease);
    }

    private void initializeMIDI() {
        try {
            track = sequence.createTrack();

            Synthesizer synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            midiChannel = synthesizer.getChannels()[0]; // Use the first channel
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void drawGrid() {
        GraphicsContext gc = this.getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        // Draw piano keys (horizontal lines with colors for white and black keys)
        for (int i = 0; i < getHeight(); i += KEY_HEIGHT) {
            int noteInOctave = (i / KEY_HEIGHT) % 12;

            // Determine the key color
            if (noteInOctave == 0 || noteInOctave == 2 || noteInOctave == 4 || noteInOctave == 5 ||
                    noteInOctave == 7 || noteInOctave == 9 || noteInOctave == 11) {
                gc.setFill(Color.WHITE);
            } else {
                gc.setFill(Color.DARKSLATEGRAY);
            }

            gc.fillRect(0, i, getWidth(), KEY_HEIGHT);

            // Draw the horizontal grid line
            gc.setStroke(Color.LIGHTGRAY);
            gc.strokeLine(0, i, getWidth(), i);
        }

        // Draw vertical lines for measures and beats
        int measure = 0;
        for (int i = 0; i < getWidth(); i += NOTE_WIDTH) {
            if (i % MEASURE_WIDTH == 0) {  // New measure
                gc.setStroke(Color.BLACK);
                gc.strokeLine(i, 0, i, getHeight());
                gc.fillText("M" + measure, i + 2, 10);
                measure++;
            } else if (i % (NOTE_WIDTH / 2) == 0) { // New beat (half the previous interval)
                gc.setStroke(Color.LIGHTGRAY);
                gc.strokeLine(i, 0, i, getHeight());
            }
        }
    }

    private void handleMouseClick(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();

        Note clickedNote = getNoteAt(x, y);

        if (event.getButton() == MouseButton.SECONDARY) {
            if (clickedNote != null) {
                notes.remove(clickedNote);
                updateTrack();
                drawNotes();
            }
            event.consume();
        } else {
            if (clickedNote == null) {
                addNoteAt(x, y);
            }
        }
    }

    private void handleMousePress(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        selectedNote = getNoteAt(x, y);
        if (selectedNote != null && isNearEdge(x, selectedNote)) {
            resizing = true;
        }
    }

    private void handleMouseDrag(MouseEvent event) {
        if (selectedNote != null) {
            double x = event.getX();
            double y = event.getY();
            if (resizing) {
                double newWidth = Math.round((x - selectedNote.getX() + NOTE_WIDTH / 2) / NOTE_WIDTH) * NOTE_WIDTH;
                selectedNote.setWidth(newWidth);
            } else {
                double newX = Math.round((x - NOTE_WIDTH / 2) / NOTE_WIDTH) * NOTE_WIDTH;
                double newY = Math.round((y - KEY_HEIGHT / 2) / KEY_HEIGHT) * KEY_HEIGHT;
                selectedNote.setX(newX);
                selectedNote.setY(newY);
            }
            updateTrack();
            drawNotes();
        }
    }

    private void handleMouseRelease(MouseEvent event) {
        selectedNote = null;
        resizing = false;
    }

    private Note getNoteAt(double x, double y) {
        for (Note note : notes) {
            if (x >= note.getX() && x <= note.getX() + note.getWidth() &&
                    y >= note.getY() && y <= note.getY() + note.getHeight()) {
                return note;
            }
        }
        return null;
    }

    private boolean isNearEdge(double x, Note note) {
        return x >= note.getX() + note.getWidth() - RESIZE_MARGIN && x <= note.getX() + note.getWidth() + RESIZE_MARGIN;
    }

    private void addNoteAt(double x, double y) {
        double snappedX = Math.round(x / NOTE_WIDTH) * NOTE_WIDTH;
        double snappedY = Math.round(y / KEY_HEIGHT) * KEY_HEIGHT;
        Note note = new Note(snappedX, snappedY, NOTE_WIDTH, KEY_HEIGHT);
        int pitch = (int)((getHeight() - note.getY()) / KEY_HEIGHT) + MIN_MIDI_PITCH;
        playNoteImmediately(pitch);
        notes.add(note);
        updateTrack();
        drawNotes();
    }

    private void playNoteImmediately(int pitch) {
        new Thread(() -> {
            try {
                midiChannel.noteOn(pitch, 64);
                Thread.sleep(500);
                midiChannel.noteOff(pitch);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void drawNotes() {
        GraphicsContext gc = this.getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        drawGrid();
        gc.setFill(Color.BLUE);
        for (Note note : notes) {
            gc.fillRect(note.getX(), note.getY(), note.getWidth(), note.getHeight());
        }
    }

    private void addMIDINoteEvent(int command, int channel, int pitch, int velocity, long tick) {
        try {
            ShortMessage message = new ShortMessage();
            message.setMessage(command, channel, pitch, velocity);
            MidiEvent event = new MidiEvent(message, tick);
            track.add(event);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    private void addNoteToTrack(Note note) {
        int pitch = (int)((getHeight() - note.getY()) / KEY_HEIGHT) + MIN_MIDI_PITCH;
        int startTime = (int)(note.getX() / NOTE_WIDTH);
        int duration = (int)(note.getWidth() / NOTE_WIDTH);

        addMIDINoteEvent(ShortMessage.NOTE_ON, 0, pitch, 64, (long)startTime);
        addMIDINoteEvent(ShortMessage.NOTE_OFF, 0, pitch, 64, (long)(startTime + duration));
    }

    private void updateTrack() {
        sequence.deleteTrack(track);
        track = sequence.createTrack();
        for (Note note : notes) {
            addNoteToTrack(note);
        }
    }

    public void play() {
        try {
            sequencer.setSequence(sequence);
            sequencer.start();
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (sequencer.isRunning()) {
            sequencer.stop();
        }
        sequencer.setTickPosition(0);
    }

    public void changeTempo(float tempoMultiplier) {
        sequencer.setTempoFactor(sequencer.getTempoFactor() * tempoMultiplier);
    }
}
