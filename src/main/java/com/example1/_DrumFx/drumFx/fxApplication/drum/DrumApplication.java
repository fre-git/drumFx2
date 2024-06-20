package com.example1._DrumFx.drumFx.fxApplication.drum;

import com.example1._DrumFx.drumFx.fxApplication.pianoRollNew.PianoRoll;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class DrumApplication extends Application {
    private MidiHandler midiHandler;
    private PianoRoll pianoRoll;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("BeatBox");

        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: #373737");

        midiHandler = new MidiHandler();
//        pianoRoll = new PianoRoll(800, 800);

        Gui gui = new Gui(borderPane, midiHandler, pianoRoll);

        Scene scene = new Scene(borderPane, 650, 300);


        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);

        primaryStage.show();
    }
}
