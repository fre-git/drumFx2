package com.example1._DrumFx.drumFx.aFxApplication.finalForm.view;

import com.example1._DrumFx.drumFx.aFxApplication.finalForm.model.Note;
import com.example1._DrumFx.drumFx.aFxApplication.finalForm.model.PianoRoll;
import com.example1._DrumFx.drumFx.aFxApplication.finalForm.model.PianoConstants;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PianoRollEditor extends Canvas {

    private List<Note> notes;
    private Note selectedNote = null;
    private boolean resizing = false;

    private PianoRoll pianoRoll;


    public PianoRollEditor(PianoRoll pianoRoll) {
        super(PianoConstants.MEASURE_WIDTH * 4, 1720);
        this.pianoRoll = pianoRoll;
        notes = new ArrayList<>();
        drawPiano();
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleMouseClick);
        this.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePress);
        this.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handleMouseDrag);
        this.addEventHandler(MouseEvent.MOUSE_RELEASED, this::handleMouseRelease);
    }

    // draws the grid lines for the piano roll
    private void drawPiano() {
        GraphicsContext gc = this.getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        for (int i = 0; i < getHeight(); i += PianoConstants.KEY_HEIGHT) {
            int noteInOctave = (i / PianoConstants.KEY_HEIGHT) % 12;
            if (noteInOctave == 0 || noteInOctave == 2 || noteInOctave == 4 || noteInOctave == 5 || noteInOctave == 7 || noteInOctave == 9 || noteInOctave == 11) {
                // calors light keys
                gc.setFill(Color.valueOf("#dddddd"));
            } else {
                //colors dark keys
                gc.setFill(Color.valueOf("#c8c8c8"));
            }
            gc.fillRect(0, i, getWidth(), PianoConstants.KEY_HEIGHT);
            gc.setStroke(Color.valueOf("#c8c8c8"));
            gc.strokeLine(0, i, getWidth(), i);
        }

        int measure = 0;
        for (int i = 0; i < getWidth(); i += PianoConstants.NOTE_WIDTH) {
            if (i % PianoConstants.MEASURE_WIDTH == 0) {
                gc.setStroke(Color.valueOf("#373737"));
                gc.strokeLine(i, 0, i, getHeight());
                gc.fillText("M" + measure, i + 2, 10);
                measure++;
            } else if (i % (PianoConstants.NOTE_WIDTH / 2) == 0) {
                gc.setStroke(Color.LIGHTGRAY);
                gc.strokeLine(i, 0, i, getHeight());
            }
        }
    }

    // adds or removes a note based on mouse clicked
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

    // selects a note if it is clicked and prepares for resizing
    private void handleMousePress(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        selectedNote = getNoteAt(x, y);
        if (selectedNote != null && isNearEdge(x, selectedNote)) {
            resizing = true;
        }
    }

    // moves or resizes the selected note
    private void handleMouseDrag(MouseEvent event) {
        if (selectedNote != null) {
            double x = event.getX();
            double y = event.getY();
            if (resizing) {
                double newWidth = Math.round((x - selectedNote.getX() + PianoConstants.NOTE_WIDTH / 2) / PianoConstants.NOTE_WIDTH) * PianoConstants.NOTE_WIDTH;
                selectedNote.setWidth(newWidth);
            } else {
                double newX = Math.round((x - PianoConstants.NOTE_WIDTH / 2) / PianoConstants.NOTE_WIDTH) * PianoConstants.NOTE_WIDTH;
                double newY = Math.round((y - PianoConstants.KEY_HEIGHT / 2) / PianoConstants.KEY_HEIGHT) * PianoConstants.KEY_HEIGHT;
                selectedNote.setX(newX);
                selectedNote.setY(newY);
            }
            updateTrack();
            drawNotes();
        }
    }

    // resets the selected note and resizing boolean
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
        return x >= note.getX() + note.getWidth() - PianoConstants.RESIZE_MARGIN && x <= note.getX() + note.getWidth() + PianoConstants.RESIZE_MARGIN;
    }


    private void addNoteAt(double x, double y) {
        double snappedX = Math.floor(x / PianoConstants.NOTE_WIDTH) * PianoConstants.NOTE_WIDTH;
        double snappedY = Math.floor(y / PianoConstants.KEY_HEIGHT) * PianoConstants.KEY_HEIGHT;
        Note note = new Note(snappedX, snappedY, PianoConstants.NOTE_WIDTH, PianoConstants.KEY_HEIGHT);
        int pitch = (int) ((getHeight() - note.getY()) / PianoConstants.KEY_HEIGHT) + PianoConstants.MIN_MIDI_PITCH;
        playNoteWhenDrawn(pitch);
        notes.add(note);
        updateTrack();
        drawNotes();
    }

    // plays the note when it is drawn on the piano roll
    private void playNoteWhenDrawn(int pitch) {
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
        drawPiano();
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
            pianoRoll.getRecordingController().addMidiEvent(event); // Add event to recording
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    private void addNoteToTrack(Note note) {
        int pitch = (int) ((getHeight() - note.getY()) / PianoConstants.KEY_HEIGHT) + PianoConstants.MIN_MIDI_PITCH;
        int startTime = (int) (note.getX() / PianoConstants.NOTE_WIDTH);
        int duration = (int) (note.getWidth() / PianoConstants.NOTE_WIDTH);
        addMIDINoteEvent(ShortMessage.NOTE_ON, 0, pitch, 64, (long) startTime * PianoConstants.RESOLUTION);
        addMIDINoteEvent(ShortMessage.NOTE_OFF, 0, pitch, 64, (long) (startTime + duration) * PianoConstants.RESOLUTION);
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
}
