package com.example1._DrumFx.drumFx.fxApplication.pianoRoll;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MidiPianoRollOld extends Application {
    @Override
    public void start(Stage primaryStage) {
        double canvasWidth = 800;
        double canvasHeight = 400;

        // Create Piano Roll and Keyboard
        PianoRollOld pianoRoll = new PianoRollOld(canvasWidth, canvasHeight);
        KeyboardOld keyboard = new KeyboardOld(canvasHeight);

        // Create control buttons
        Button playButton = new Button("Play");
        Button stopButton = new Button("Stop");

        playButton.setOnAction(e -> pianoRoll.play());
        stopButton.setOnAction(e -> pianoRoll.stop());

        // Layout for controls
        VBox controls = new VBox(10, playButton, stopButton);

        // Layout for piano roll and keyboard
        HBox rollAndKeyboard = new HBox(10, keyboard, pianoRoll);

        // Wrap the piano roll and keyboard in a ScrollPane for scrolling functionality
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(rollAndKeyboard);
        scrollPane.setFitToWidth(true);

        // Create the root layout
        VBox root = new VBox(100, scrollPane, controls);

        // Create the scene and set it on the stage
        Scene scene = new Scene(root, canvasWidth + 100, canvasHeight + 20);
        primaryStage.setTitle("MIDI Piano Roll");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
