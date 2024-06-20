package com.example1._DrumFx.drumFx.fxApplication.together;

import com.example1._DrumFx.drumFx.fxApplication.drum.Gui;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.sound.midi.*;

import static javafx.application.Application.launch;

public class DawApp extends Application {
    private Midihandler1 midiHandler;
    private PianoRoll1 pianoRoll;
    private Sequencer sharedSequencer;
    private Sequence sharedSequence;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Combined MIDI Application");

        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: #373737");

        // Initialize shared Sequencer and Sequence
        try {
            sharedSequencer = MidiSystem.getSequencer();
            sharedSequencer.open();
            sharedSequence = new Sequence(Sequence.PPQ, 24);
        } catch (MidiUnavailableException | InvalidMidiDataException e) {
            e.printStackTrace();
        }

        // Initialize Drum GUI
        midiHandler = new Midihandler1(sharedSequencer, sharedSequence);

        // Initialize Piano Roll
        pianoRoll = new PianoRoll1(sharedSequencer, sharedSequence);

        GUI1 drumGui = new GUI1(borderPane, midiHandler, pianoRoll);

        // Layout for drum and piano roll
        HBox drumBox = drumGui.createDrumBox();
        ScrollPane pianoPane = new ScrollPane();
        pianoPane.setContent(pianoRoll);
        pianoPane.setPrefViewportHeight(200);
        pianoPane.setFitToWidth(true);

        // Combine all into a VBox
        VBox combinedBox = new VBox(10, pianoPane, drumBox);

        // Set the combined box in the center of the border pane
        borderPane.setCenter(combinedBox);

        // Create the scene and set it on the stage
        Scene scene = new Scene(borderPane, 850, 600);
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
