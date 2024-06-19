package com.example1._DrumFx.drumFx.fxApplication.pianoRoll;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.List;

public class PianoRollOld extends Canvas {
    private static final double KEY_HEIGHT = 10;
    private static final int NOTE_WIDTH = 10;
    private static final int RESIZE_MARGIN = 5;
    private List<NoteOld> notes;
    private NoteOld selectedNote = null;
    private boolean resizing = false;

    private Sequencer sequencer;
    private Sequence sequence;
    private Track track;

    public PianoRollOld(double width, double height) {
        super(width, height);
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
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence = new Sequence(Sequence.PPQ, 24);
            track = sequence.createTrack();
        } catch (MidiUnavailableException | InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    private void drawGrid() {
        GraphicsContext gc = this.getGraphicsContext2D();
        // Draw horizontal lines
        for (int i = 0; i < getHeight(); i += KEY_HEIGHT) {
            gc.setStroke(Color.LIGHTGRAY);
            gc.strokeLine(0, i, getWidth(), i);
        }
        // Draw vertical lines and time signatures
        int measure = 1;
        for (int i = 0; i < getWidth(); i += NOTE_WIDTH) {
            if (i % (NOTE_WIDTH * 16) == 0) {  // New measure
                gc.setStroke(Color.BLACK);
                gc.strokeLine(i, 0, i, getHeight());
                gc.fillText(Integer.toString(measure), i + 2, 10);
                measure++;
            } else {
                gc.setStroke(Color.LIGHTGRAY);
                gc.strokeLine(i, 0, i, getHeight());
            }
        }
    }

    private void handleMouseClick(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        if (event.isSecondaryButtonDown()) {  // Right-click to delete
            deleteNoteAt(x, y);
        } else {
            addNoteAt(x, y);
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

    private NoteOld getNoteAt(double x, double y) {
        for (NoteOld note : notes) {
            if (x >= note.getX() && x <= note.getX() + note.getWidth() &&
                    y >= note.getY() && y <= note.getY() + note.getHeight()) {
                return note;
            }
        }
        return null;
    }

    private boolean isNearEdge(double x, NoteOld note) {
        return x >= note.getX() + note.getWidth() - RESIZE_MARGIN && x <= note.getX() + note.getWidth() + RESIZE_MARGIN;
    }

    private void addNoteAt(double x, double y) {
        // Snap the coordinates to inside the grid cells
        double snappedX = Math.round((x - NOTE_WIDTH / 2) / NOTE_WIDTH) * NOTE_WIDTH;
        double snappedY = Math.round((y - KEY_HEIGHT / 2) / KEY_HEIGHT) * KEY_HEIGHT;
        NoteOld note = new NoteOld(snappedX, snappedY, NOTE_WIDTH, KEY_HEIGHT);
        notes.add(note);
        updateTrack();
        drawNotes();
    }

    private void deleteNoteAt(double x, double y) {
        NoteOld note = getNoteAt(x, y);
        if (note != null) {
            notes.remove(note);
        }
        updateTrack();
        drawNotes();
    }

    private void drawNotes() {
        GraphicsContext gc = this.getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        drawGrid();
        gc.setFill(Color.BLUE);
        for (NoteOld note : notes) {
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

    private void addNoteToTrack(NoteOld note) {
        int pitch = (int)((getHeight() - note.getY()) / KEY_HEIGHT) + 21; // MIDI note number (21 is A0)
        int startTime = (int)(note.getX() / NOTE_WIDTH);
        int duration = (int)(note.getWidth() / NOTE_WIDTH);

        addMIDINoteEvent(ShortMessage.NOTE_ON, 0, pitch, 64, (long)startTime);
        addMIDINoteEvent(ShortMessage.NOTE_OFF, 0, pitch, 64, (long)(startTime + duration));
    }

    private void updateTrack() {
        sequence.deleteTrack(track);
        track = sequence.createTrack();
        for (NoteOld note : notes) {
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
}









//
//import javafx.scene.canvas.Canvas;
//import javafx.scene.canvas.GraphicsContext;
//import javafx.scene.input.MouseEvent;
//import javafx.scene.paint.Color;
//
//import javax.sound.midi.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class PianoRoll extends Canvas {
//    private static final int KEY_HEIGHT = 20;
//    private static final int NOTE_WIDTH = 10;
//    private static final int RESIZE_MARGIN = 5;
//    private List<Note> notes;
//    private Note selectedNote = null;
//    private boolean resizing = false;
//
//    private Sequencer sequencer;
//    private Sequence sequence;
//    private Track track;
//
//    public PianoRoll(double width, double height) {
//        super(width, height);
//        notes = new ArrayList<>();
//        drawGrid();
//
//        // Initialize MIDI components
//        initializeMIDI();
//
//        // Add event handlers
//        this.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleMouseClick);
//        this.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePress);
//        this.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handleMouseDrag);
//        this.addEventHandler(MouseEvent.MOUSE_RELEASED, this::handleMouseRelease);
//    }
//
//    private void initializeMIDI() {
//        try {
//            sequencer = MidiSystem.getSequencer();
//            sequencer.open();
//            sequence = new Sequence(Sequence.PPQ, 24);
//            track = sequence.createTrack();
//        } catch (MidiUnavailableException | InvalidMidiDataException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void drawGrid() {
//        GraphicsContext gc = this.getGraphicsContext2D();
//        for (int i = 0; i < getHeight(); i += KEY_HEIGHT) {
//            gc.setStroke(Color.LIGHTGRAY);
//            gc.strokeLine(0, i, getWidth(), i);
//        }
//        for (int i = 0; i < getWidth(); i += NOTE_WIDTH) {
//            gc.setStroke(Color.LIGHTGRAY);
//            gc.strokeLine(i, 0, i, getHeight());
//        }
//    }
//
//    private void handleMouseClick(MouseEvent event) {
//        double x = event.getX();
//        double y = event.getY();
//        if (event.isSecondaryButtonDown()) {  // Right-click to delete
//            deleteNoteAt(x, y);
//        } else {
//            addNoteAt(x, y);
//        }
//    }
//
//    private void handleMousePress(MouseEvent event) {
//        double x = event.getX();
//        double y = event.getY();
//        selectedNote = getNoteAt(x, y);
//        if (selectedNote != null && isNearEdge(x, selectedNote)) {
//            resizing = true;
//        }
//    }
//
//    private void handleMouseDrag(MouseEvent event) {
//        if (selectedNote != null) {
//            double x = event.getX();
//            double y = event.getY();
//            if (resizing) {
//                double newWidth = Math.round((x - selectedNote.getX()) / NOTE_WIDTH) * NOTE_WIDTH;
//                selectedNote.setWidth(newWidth);
//            } else {
//                double newX = Math.round(x / NOTE_WIDTH) * NOTE_WIDTH - NOTE_WIDTH / 2;
//                double newY = Math.round(y / KEY_HEIGHT) * KEY_HEIGHT - KEY_HEIGHT / 2;
//                selectedNote.setX(newX);
//                selectedNote.setY(newY);
//            }
//            updateTrack();
//            drawNotes();
//        }
//    }
//
//    private void handleMouseRelease(MouseEvent event) {
//        selectedNote = null;
//        resizing = false;
//    }
//
//    private Note getNoteAt(double x, double y) {
//        for (Note note : notes) {
//            if (x >= note.getX() && x <= note.getX() + note.getWidth() &&
//                    y >= note.getY() && y <= note.getY() + note.getHeight()) {
//                return note;
//            }
//        }
//        return null;
//    }
//
//    private boolean isNearEdge(double x, Note note) {
//        return x >= note.getX() + note.getWidth() - RESIZE_MARGIN && x <= note.getX() + note.getWidth() + RESIZE_MARGIN;
//    }
//
//    private void addNoteAt(double x, double y) {
//        // Snap the coordinates to inside the grid cells
//        double snappedX = Math.round((x - NOTE_WIDTH / 2) / NOTE_WIDTH) * NOTE_WIDTH;
//        double snappedY = Math.round((y - KEY_HEIGHT / 2) / KEY_HEIGHT) * KEY_HEIGHT;
//        Note note = new Note(snappedX, snappedY, NOTE_WIDTH, KEY_HEIGHT);
//        notes.add(note);
//        updateTrack();
//        drawNotes();
////        // Snap the coordinates to the grid
////        double snappedX = Math.round(x / NOTE_WIDTH) * NOTE_WIDTH - NOTE_WIDTH / 2;
////        double snappedY = Math.round(y / KEY_HEIGHT) * KEY_HEIGHT - KEY_HEIGHT / 2;
////        Note note = new Note(snappedX, snappedY, NOTE_WIDTH, KEY_HEIGHT);
////        notes.add(note);
////        updateTrack();
////        drawNotes();
//    }
//
//    private void deleteNoteAt(double x, double y) {
//        Note note = getNoteAt(x, y);
//        if (note != null) {
//            notes.remove(note);
//        }
//        updateTrack();
//        drawNotes();
//    }
//
//    private void drawNotes() {
//        GraphicsContext gc = this.getGraphicsContext2D();
//        gc.clearRect(0, 0, getWidth(), getHeight());
//        drawGrid();
//        gc.setFill(Color.BLUE);
//        for (Note note : notes) {
//            gc.fillRect(note.getX(), note.getY(), note.getWidth(), note.getHeight());
//        }
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
//        int pitch = (int)((getHeight() - note.getY()) / KEY_HEIGHT) + 21; // MIDI note number (21 is A0)
//        int startTime = (int)(note.getX() / NOTE_WIDTH);
//        int duration = (int)(note.getWidth() / NOTE_WIDTH);
//
//        addMIDINoteEvent(ShortMessage.NOTE_ON, 0, pitch, 64, (long)startTime);
//        addMIDINoteEvent(ShortMessage.NOTE_OFF, 0, pitch, 64, (long)(startTime + duration));
//    }
//
//    private void updateTrack() {
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
//}
