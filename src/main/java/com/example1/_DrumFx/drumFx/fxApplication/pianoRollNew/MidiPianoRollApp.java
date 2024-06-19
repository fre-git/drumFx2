package com.example1._DrumFx.drumFx.fxApplication.pianoRollNew;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import static javafx.application.Application.launch;

public class MidiPianoRollApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        double canvasWidth = 800;
        double canvasHeight = 400;

        // Create Piano Roll and Keyboard
        PianoRoll pianoRoll = new PianoRoll(canvasWidth, 880);

        // Create control buttons
        Button playButton = new Button("Play");
        Button stopButton = new Button("Stop");

        playButton.setOnAction(e -> pianoRoll.play());
        stopButton.setOnAction(e -> pianoRoll.stop());

        // Layout for controls
        VBox controls = new VBox(10, playButton, stopButton);

        // Layout for piano roll and keyboard
        HBox pianoBox = new HBox(10, pianoRoll);

        // Wrap the piano roll in a ScrollPane for scrolling functionality
        ScrollPane pianoPane = new ScrollPane();
        pianoPane.setContent(pianoBox);
        pianoPane.setPrefViewportHeight(200);
        pianoPane.setFitToWidth(true);

        // Create the root layout
        VBox root = new VBox(10, pianoPane, controls);

        // Create the scene and set it on the stage
        Scene scene = new Scene(root, canvasWidth + 100, canvasHeight + 20);
        primaryStage.setTitle("MIDI Piano Roll");
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
