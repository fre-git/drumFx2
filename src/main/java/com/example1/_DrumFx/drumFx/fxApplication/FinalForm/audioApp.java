package com.example1._DrumFx.drumFx.fxApplication.FinalForm;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

public class audioApp extends Application {
    private Sequencer sharedSequencer;
    private Sequence sharedSequence;
    private MidiManager midiManager;
    private PianoRollEditor pianoRollEditor;

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

        midiManager = new MidiManager(sharedSequencer, sharedSequence);
        pianoRollEditor = new PianoRollEditor(sharedSequencer, sharedSequence);

        // Set the same tempo for both
        int bpm = 120;
        midiManager.setTempo(bpm);
        pianoRollEditor.setTempo(bpm);

        // Set sequence length (adjust measures as needed)
        int drumMeasures = 4;
        int pianoMeasures = drumMeasures * 4;
        pianoRollEditor.updateTrackLength(pianoMeasures);

        ScrollPane pianoRollScrollPane = new ScrollPane(pianoRollEditor);
        pianoRollScrollPane.setFitToHeight(true);
        pianoRollScrollPane.setFitToWidth(true);

        DAWGui dawGui = new DAWGui(borderPane, midiManager, pianoRollScrollPane, pianoRollEditor);
        borderPane.setCenter(dawGui.getCombinedBox());

        Scene scene = new Scene(borderPane, 1200, 800);
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();


//        primaryStage.setTitle("Audio DAW");
//
//        BorderPane borderPane = new BorderPane();
//        borderPane.setStyle("-fx-background-color: #373737");
//
//        // Initialize shared Sequencer and Sequence
//        try {
//            sharedSequencer = MidiSystem.getSequencer();
//            sharedSequencer.open();
//            sharedSequence = new Sequence(Sequence.PPQ, 24);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        // Initialize MIDI Manager
//        midiManager = new MidiManager(sharedSequencer, sharedSequence);
//
//        // Initialize Piano Roll Editor
//        pianoRollEditor = new PianoRollEditor(sharedSequencer, sharedSequence);
//
//        // Make the Piano Roll Editor scrollable
//        ScrollPane pianoRollScrollPane = new ScrollPane(pianoRollEditor);
//        pianoRollScrollPane.setFitToHeight(true);
//        pianoRollScrollPane.setFitToWidth(true);
//
//        // Initialize GUI
//        DAWGui dawGui = new DAWGui(borderPane, midiManager, pianoRollScrollPane);
//
//        // Set the GUI layout
//        borderPane.setCenter(dawGui.getCombinedBox());
//
//        // Create the scene and set it on the stage
//        Scene scene = new Scene(borderPane, 1200, 800);
//        primaryStage.setMaximized(true);
//        primaryStage.setScene(scene);
//        primaryStage.show();
    }
}
