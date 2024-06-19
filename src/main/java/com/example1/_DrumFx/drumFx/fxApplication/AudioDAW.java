package com.example1._DrumFx.drumFx.fxApplication;

import com.example1._DrumFx.drumFx.fxApplication.drum.Gui;
import com.example1._DrumFx.drumFx.fxApplication.drum.MidiHandler;
import com.example1._DrumFx.drumFx.fxApplication.pianoRollNew.PianoRoll;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AudioDAW extends Application {
    private MidiHandler midiHandler;
    private PianoRoll pianoRoll;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Combined MIDI Application");

        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: #373737");

        // Initialize Drum GUI
        midiHandler = new MidiHandler();

        // Initialize Piano Roll
        double canvasWidth = 800;
//        double canvasHeight = 400;
        pianoRoll = new PianoRoll(canvasWidth, 880);

        Gui drumGui = new Gui(borderPane, midiHandler, pianoRoll);


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
