package com.example1._DrumFx.drumFx.fxApplication.finalEstForm.view;

import com.example1._DrumFx.drumFx.fxApplication.finalEstForm.model.Note;
import com.example1._DrumFx.drumFx.fxApplication.finalEstForm.model.PianoRoll;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

import javax.sound.midi.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PianoRollEditor extends Canvas {

    private static final int KEY_HEIGHT = 15;
    private static final int NOTE_WIDTH = 55;
    private static final int BEATS_PER_MEASURE = 4;
    private static final int MEASURE_WIDTH = NOTE_WIDTH * BEATS_PER_MEASURE;
    private static final int RESIZE_MARGIN = 5;
    private static final int MIN_MIDI_PITCH = 21;
    private static final int MAX_MIDI_PITCH = 108;
    private static final int RESOLUTION = 8;

    private List<Note> notes;
    private Note selectedNote = null;
    private boolean resizing = false;

    private PianoRoll pianoRoll;

    private ObjectMapper objectMapper;

    public PianoRollEditor(PianoRoll pianoRoll) {
        super(MEASURE_WIDTH * 4, 1720);
        this.pianoRoll = pianoRoll;
        notes = new ArrayList<>();
        this.objectMapper = new ObjectMapper();
        drawGrid();
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleMouseClick);
        this.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePress);
        this.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handleMouseDrag);
        this.addEventHandler(MouseEvent.MOUSE_RELEASED, this::handleMouseRelease);
    }

    private void drawGrid() {
        GraphicsContext gc = this.getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        for (int i = 0; i < getHeight(); i += KEY_HEIGHT) {
            int noteInOctave = (i / KEY_HEIGHT) % 12;
            if (noteInOctave == 0 || noteInOctave == 2 || noteInOctave == 4 || noteInOctave == 5 || noteInOctave == 7 || noteInOctave == 9 || noteInOctave == 11) {
                gc.setFill(Color.valueOf("#dddddd"));
            } else {
                gc.setFill(Color.valueOf("#c8c8c8"));
            }
            gc.fillRect(0, i, getWidth(), KEY_HEIGHT);
            gc.setStroke(Color.valueOf("#c8c8c8"));
            gc.strokeLine(0, i, getWidth(), i);
        }

        int measure = 0;
        for (int i = 0; i < getWidth(); i += NOTE_WIDTH) {
            if (i % MEASURE_WIDTH == 0) {
                gc.setStroke(Color.valueOf("#373737"));
                gc.strokeLine(i, 0, i, getHeight());
                gc.fillText("M" + measure, i + 2, 10);
                measure++;
            } else if (i % (NOTE_WIDTH / 2) == 0) {
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
            if (x >= note.getX() && x <= note.getX() + note.getWidth() && y >= note.getY() && y <= note.getY() + note.getHeight()) {
                return note;
            }
        }
        return null;
    }

    private boolean isNearEdge(double x, Note note) {
        return x >= note.getX() + note.getWidth() - RESIZE_MARGIN && x <= note.getX() + note.getWidth() + RESIZE_MARGIN;
    }

    private void addNoteAt(double x, double y) {
        double snappedX = Math.floor(x / NOTE_WIDTH) * NOTE_WIDTH;
        double snappedY = Math.floor(y / KEY_HEIGHT) * KEY_HEIGHT;
        Note note = new Note(snappedX, snappedY, NOTE_WIDTH, KEY_HEIGHT);
        int pitch = (int) ((getHeight() - note.getY()) / KEY_HEIGHT) + MIN_MIDI_PITCH;
        playNoteImmediately(pitch);
        notes.add(note);
        updateTrack();
        drawNotes();
    }

    private void playNoteImmediately(int pitch) {
        new Thread(() -> {
            try {
                pianoRoll.getMidiChannel().noteOn(pitch, 64);
                Thread.sleep(500);
                pianoRoll.getMidiChannel().noteOff(pitch);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void drawNotes() {
        GraphicsContext gc = this.getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        drawGrid();
        gc.setFill(Color.valueOf("#fe5245"));
        for (Note note : notes) {
            gc.fillRect(note.getX(), note.getY(), note.getWidth(), note.getHeight());
        }
    }

    private void addMIDINoteEvent(int command, int channel, int pitch, int velocity, long tick) {
        try {
            ShortMessage message = new ShortMessage();
            message.setMessage(command, channel, pitch, velocity);
            MidiEvent event = new MidiEvent(message, tick);
            pianoRoll.getTrack().add(event);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    private void addNoteToTrack(Note note) {
        int pitch = (int) ((getHeight() - note.getY()) / KEY_HEIGHT) + MIN_MIDI_PITCH;
        int startTime = (int) (note.getX() / NOTE_WIDTH);
        int duration = (int) (note.getWidth() / NOTE_WIDTH);
        addMIDINoteEvent(ShortMessage.NOTE_ON, 0, pitch, 64, (long) startTime * RESOLUTION);
        addMIDINoteEvent(ShortMessage.NOTE_OFF, 0, pitch, 64, (long) (startTime + duration) * RESOLUTION);
    }

    private void updateTrack() {
        pianoRoll.getSequence().deleteTrack(pianoRoll.getTrack());
        pianoRoll.setTrack(pianoRoll.getSequence().createTrack());
        for (Note note : notes) {
            addNoteToTrack(note);
        }
    }

    public void play() {
        try {
            pianoRoll.getSequencer().setSequence(pianoRoll.getSequence());
            pianoRoll.getSequencer().start();
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (pianoRoll.getSequencer().isRunning()) {
            pianoRoll.getSequencer().stop();
        }
        pianoRoll.getSequencer().setTickPosition(0);
    }

    public String saveState() {
        try {
            return objectMapper.writeValueAsString(notes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void loadState(String state) {
        try {
            List<Note> loadedNotes = objectMapper.readValue(state, new TypeReference<List<Note>>() {});
            this.notes.clear();
            this.notes.addAll(loadedNotes);
            updateTrack();
            drawNotes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double calculateDuration() {
        double maxEndTime = 0;
        for (Note note : notes) {
            double noteEndTime = note.getX() + note.getWidth();
            if (noteEndTime > maxEndTime) {
                maxEndTime = noteEndTime;
            }
        }
        return maxEndTime / (NOTE_WIDTH * 4);
    }




//    private PianoRoll pianoRoll;
//
//    private static final int KEY_HEIGHT = 15;
//    private static final int NOTE_WIDTH = 55;
//    private static final int BEATS_PER_MEASURE = 4;
//    private static final int MEASURE_WIDTH = NOTE_WIDTH * BEATS_PER_MEASURE;
//    private static final int RESIZE_MARGIN = 5;
//
//    private Note selectedNote = null;
//    private boolean resizing = false;
//
//    public PianoRollEditor(PianoRoll pianoRoll) {
//        super(MEASURE_WIDTH * 4, 1720);
//        this.pianoRoll = pianoRoll;
//        drawGrid();
//        this.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleMouseClick);
//        this.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePress);
//        this.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handleMouseDrag);
//        this.addEventHandler(MouseEvent.MOUSE_RELEASED, this::handleMouseRelease);
//    }
//
//    private void drawGrid() {
//        GraphicsContext gc = this.getGraphicsContext2D();
//        gc.clearRect(0, 0, getWidth(), getHeight());
//        for (int i = 0; i < getHeight(); i += KEY_HEIGHT) {
//            int noteInOctave = (i / KEY_HEIGHT) % 12;
//            if (noteInOctave == 0 || noteInOctave == 2 || noteInOctave == 4 || noteInOctave == 5 || noteInOctave == 7 || noteInOctave == 9 || noteInOctave == 11) {
//                gc.setFill(Color.valueOf("#dddddd"));
//            } else {
//                gc.setFill(Color.valueOf("#c8c8c8"));
//            }
//            gc.fillRect(0, i, getWidth(), KEY_HEIGHT);
//            gc.setStroke(Color.valueOf("#c8c8c8"));
//            gc.strokeLine(0, i, getWidth(), i);
//        }
//
//        int measure = 0;
//        for (int i = 0; i < getWidth(); i += NOTE_WIDTH) {
//            if (i % MEASURE_WIDTH == 0) {
//                gc.setStroke(Color.valueOf("#373737"));
//                gc.strokeLine(i, 0, i, getHeight());
//                gc.fillText("M" + measure, i + 2, 10);
//                measure++;
//            } else if (i % (NOTE_WIDTH / 2) == 0) {
//                gc.setStroke(Color.LIGHTGRAY);
//                gc.strokeLine(i, 0, i, getHeight());
//            }
//        }
//    }
//
//    private void handleMouseClick(MouseEvent event) {
//        double x = event.getX();
//        double y = event.getY();
//        if (event.getButton() == MouseButton.SECONDARY) {
//            pianoRoll.removeNoteAt(x, y);
//            drawNotes();
//            event.consume();
//        } else {
//            if (pianoRoll.getNoteAt(x, y) == null) {
//                pianoRoll.addNoteAt(x, y);
//                drawNotes();
//            }
//        }
//    }
//
//    private void handleMousePress(MouseEvent event) {
//        double x = event.getX();
//        double y = event.getY();
//        selectedNote = pianoRoll.getNoteAt(x, y);
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
//                double newWidth = Math.round((x - selectedNote.getX() + NOTE_WIDTH / 2) / NOTE_WIDTH) * NOTE_WIDTH;
//                selectedNote.setWidth(newWidth);
//            } else {
//                double newX = Math.round((x - NOTE_WIDTH / 2) / NOTE_WIDTH) * NOTE_WIDTH;
//                double newY = Math.round((y - KEY_HEIGHT / 2) / KEY_HEIGHT) * KEY_HEIGHT;
//                selectedNote.setX(newX);
//                selectedNote.setY(newY);
//            }
//            pianoRoll.updateTrack();
//            drawNotes();
//        }
//    }
//
//    private void handleMouseRelease(MouseEvent event) {
//        selectedNote = null;
//        resizing = false;
//    }
//
//    private boolean isNearEdge(double x, Note note) {
//        return x >= note.getX() + note.getWidth() - RESIZE_MARGIN && x <= note.getX() + note.getWidth() + RESIZE_MARGIN;
//    }
//
//    private void drawNotes() {
//        GraphicsContext gc = this.getGraphicsContext2D();
//        gc.clearRect(0, 0, getWidth(), getHeight());
//        drawGrid();
//        gc.setFill(Color.valueOf("#fe5245"));
//        for (Note note : pianoRoll.getNotes()) {
//            gc.fillRect(note.getX(), note.getY(), note.getWidth(), note.getHeight());
//        }
//    }
//
//    public void play() {
//        pianoRoll.play();
//    }
//
//    public void stop() {
//        pianoRoll.stop();
//    }
//
//    public void changeTempo(int tempoChange) {
//        pianoRoll.changeTempo(tempoChange);
//    }
//
//    public void setTempo(int bpm) {
//        pianoRoll.setTempo(bpm);
//    }
//
//    public void loadState(String state) {
//        pianoRoll.loadState(state);
//        drawNotes();
//    }
//
//    public String saveState() {
//        return pianoRoll.saveState();
//    }
//
//    public double calculateDuration() {
//        return pianoRoll.calculateDuration();
//    }
}
