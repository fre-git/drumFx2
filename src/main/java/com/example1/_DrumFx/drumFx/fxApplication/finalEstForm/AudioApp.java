package com.example1._DrumFx.drumFx.fxApplication.finalEstForm;

import com.example1._DrumFx.drumFx.fxApplication.finalEstForm.controller.MidiManager;
import com.example1._DrumFx.drumFx.fxApplication.finalEstForm.controller.TempoController;
import com.example1._DrumFx.drumFx.fxApplication.finalEstForm.model.MidiToAudioRecorder;
import com.example1._DrumFx.drumFx.fxApplication.finalEstForm.model.PianoRoll;
import com.example1._DrumFx.drumFx.fxApplication.finalEstForm.view.DAWGui;
import com.example1._DrumFx.drumFx.fxApplication.finalEstForm.view.PianoRollEditor;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.LineUnavailableException;
import java.io.File;
import java.io.IOException;

@Getter
@Setter
public class AudioApp extends Application {
    private Sequencer sharedSequencer;
    private Sequence sharedSequence;
    private MidiManager midiManager;
    private PianoRollEditor pianoRollEditor;
    private PianoRoll pianoRoll;
    private TempoController tempoController;

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
        pianoRoll = new PianoRoll(sharedSequencer, sharedSequence);
        pianoRollEditor = new PianoRollEditor(pianoRoll);
        tempoController = new TempoController(midiManager, pianoRoll);

        int bpm = 120;
        midiManager.setTempo(bpm);

        int drumMeasures = 4;
        int pianoMeasures = drumMeasures * 4;
        pianoRoll.updateTrackLength(pianoMeasures);

        ScrollPane pianoRollScrollPane = new ScrollPane(pianoRollEditor);
        pianoRollScrollPane.setFitToHeight(true);
        pianoRollScrollPane.setFitToWidth(true);

        DAWGui dawGui = new DAWGui(this, borderPane, midiManager, pianoRollScrollPane, pianoRollEditor, pianoRoll);
        borderPane.setBottom(dawGui.getCombinedBox());

        Scene scene = new Scene(borderPane, 1200, 800);
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public void saveMidiFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save MIDI File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MIDI files", "*.mid"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                midiManager.addCurrentTempoToTrack();
                MidiSystem.write(sharedSequence, 1, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
