package com.example1._DrumFx.drumFx.aFxApplication.finalForm;

import com.example1._DrumFx.drumFx.aFxApplication.finalForm.controller.MidiController;
import com.example1._DrumFx.drumFx.aFxApplication.finalForm.controller.TempoController;
import com.example1._DrumFx.drumFx.aFxApplication.finalForm.model.PianoRoll;
import com.example1._DrumFx.drumFx.aFxApplication.finalForm.view.DAWGui;
import com.example1._DrumFx.drumFx.aFxApplication.finalForm.view.PianoRollEditor;
import com.example1._DrumFx.drumFx.aFxApplication.finalForm.controller.RecordingController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import javax.sound.midi.*;
import java.io.*;

@Getter
@Setter
public class AudioApp extends Application {
    private Sequencer sharedSequencer;
    private Sequence sharedSequence;
    private MidiController midiController;
    private PianoRollEditor pianoRollEditor;
    private PianoRoll pianoRoll;
    private TempoController tempoController;
    private RecordingController recordingController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Audio DAW");

        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: #373737");

        try {
            sharedSequencer = MidiSystem.getSequencer();
            sharedSequencer.open();
            sharedSequence = new Sequence(Sequence.PPQ, 24);

        } catch (Exception e) {
            e.printStackTrace();
        }

        recordingController = new RecordingController(sharedSequencer);

        midiController = new MidiController(sharedSequencer, sharedSequence, recordingController);
        pianoRoll = new PianoRoll(sharedSequencer, sharedSequence, recordingController);
        pianoRollEditor = new PianoRollEditor(pianoRoll);
        tempoController = new TempoController(midiController, pianoRoll);

        int bpm = 120;
        midiController.setTempo(bpm);

        int drumMeasures = 4;
        int pianoMeasures = drumMeasures * 4;
        pianoRoll.updateTrackLength(pianoMeasures);

        ScrollPane pianoRollScrollPane = new ScrollPane(pianoRollEditor);
        pianoRollScrollPane.setFitToHeight(true);
        pianoRollScrollPane.setFitToWidth(true);

        DAWGui dawGui = new DAWGui(this, borderPane, midiController, pianoRollScrollPane, pianoRollEditor, pianoRoll, recordingController);
        borderPane.setBottom(dawGui.getCombinedBox());

        Scene scene = new Scene(borderPane, 1500, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    //Starts recording a MIDI file by showing a file chooser to save the recording
    public void startRecording() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Recording");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MIDI files", "*.mid"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            recordingController.startRecording(file);
        }
    }

    public void stopRecording() {
        recordingController.stopRecording();
    }

    public void saveMidiFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save MIDI File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MIDI files", "*.mid"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                midiController.addCurrentTempoToTrack();
                MidiSystem.write(sharedSequence, 1, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}