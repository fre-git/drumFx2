package com.example1._DrumFx.drumFx.aFxApplication.finalForm.view;

import com.example1._DrumFx.drumFx.aFxApplication.finalForm.AudioApp;
import com.example1._DrumFx.drumFx.aFxApplication.finalForm.controller.MidiController;
import com.example1._DrumFx.drumFx.aFxApplication.finalForm.controller.RecordingController;
import com.example1._DrumFx.drumFx.aFxApplication.finalForm.model.PianoRoll;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DAWGui {

    private final AudioApp app;
    private final BorderPane borderPane;
    private final MidiController midiController;
    private final ScrollPane pianoScrollPane;
    private final List<ToggleButton> buttonList = new ArrayList<>();
    private final String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acoustic Snare", "Crash Cymbal", "Hand Clap", "High Tom", "Low-Mid Tom"};
    private HBox combinedBox;
    private PianoRollEditor pianoRollEditor;
    private PianoRoll pianoRoll;
    private boolean isRecording = false;

    private RecordingController recordingController;

    public DAWGui(AudioApp app, BorderPane borderPane, MidiController midiController, ScrollPane pianoScrollPane,
                  PianoRollEditor pianoRollEditor, PianoRoll pianoRoll, RecordingController recordingController) {
        this.app = app;
        this.borderPane = borderPane;
        this.midiController = midiController;
        this.pianoScrollPane = pianoScrollPane;
        pianoScrollPane.setVvalue(1);
        this.pianoRollEditor = pianoRollEditor;
        this.pianoRoll = pianoRoll;
        this.recordingController = recordingController;

        createMenuBar();
        drawDrumGrid();
        VBox mainVBox = new VBox(combinedBox, createControlBar());
        mainVBox.setSpacing(10);
        borderPane.setCenter(mainVBox);
    }

    private void createMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem saveMenuItem = new MenuItem("Save");
        saveMenuItem.setOnAction(e -> app.saveMidiFile());
        fileMenu.getItems().add(saveMenuItem);

        menuBar.getMenus().add(fileMenu);
        borderPane.setTop(menuBar);
    }

    private HBox createControlBar() {
        HBox controlBar = new HBox();
        controlBar.setSpacing(20);
        controlBar.setPadding(new Insets(10));
        controlBar.setStyle("-fx-background-color: #333333; -fx-border-color: #222222; -fx-border-width: 1px;");
        controlBar.setPrefHeight(80);

        Button startButton = new Button();
        Text textStart = new Text("\u25B6");
        textStart.setFont(Font.font(36));
        textStart.setFill(Color.GREEN);
        startButton.setGraphic(textStart);
        startButton.setMinSize(50, 50);
        startButton.setStyle("-fx-background-color: #444444; -fx-text-fill: #DDDDDD;");
        startButton.setOnAction(e -> handleStartButtonAction());

        Button stopButton = new Button();
        Text textStop = new Text("\u23F9");
        textStop.setFill(Color.RED);
        textStop.setFont(Font.font(24));
        stopButton.setGraphic(textStop);
        stopButton.setMinSize(50, 50);
        stopButton.setStyle("-fx-background-color: #444444; -fx-text-fill: #DDDDDD;");
        stopButton.setOnAction(e -> {
            midiController.stop();
            pianoRollEditor.stop();
        });

        Button recordButton = new Button("Record Live");
        recordButton.setMinSize(50, 50);
        recordButton.setStyle("-fx-background-color: #444444; -fx-text-fill: #DDDDDD;");
        recordButton.setOnAction(e -> {
            if (isRecording) {
                app.stopRecording();
                isRecording = false;
                recordButton.setText("Record Live");
            } else {
                app.startRecording();
                isRecording = true;
                recordButton.setText("Stop Recording");
            }
        });

        Button upTempoButton = new Button("Tempo Up");
        upTempoButton.setMinSize(80, 50);
        upTempoButton.setStyle("-fx-background-color: #444444; -fx-text-fill: #DDDDDD;");
        upTempoButton.setOnAction(e -> app.getTempoController().changeTempo(1));

        Button downTempoButton = new Button("Tempo Down");
        downTempoButton.setMinSize(80, 50);
        downTempoButton.setStyle("-fx-background-color: #444444; -fx-text-fill: #DDDDDD;");
        downTempoButton.setOnAction(e -> app.getTempoController().changeTempo(-1));

        controlBar.getChildren().addAll(startButton, stopButton, recordButton, upTempoButton, downTempoButton);
        return controlBar;
    }

    //Builds the MIDI track, starts the sequencer, and plays the piano roll editor
    private void handleStartButtonAction() {
        midiController.buildTrack(getButtonState());
        midiController.startSequencer();
        pianoRollEditor.play();
        if (isRecording) {
            recordingController.updateTickOffset();
        }
    }


    // draws drum machine
    public void drawDrumGrid() {
        VBox labelBox = new VBox(14);
        labelBox.setPadding(new Insets(12));
        labelBox.setStyle("-fx-background-color: #222222; -fx-border-color: #222222;");
        for (String instrumentName : instrumentNames) {
            Label instrumentLabel = new Label(instrumentName);
            instrumentLabel.setTextFill(Color.WHITE);
            instrumentLabel.setFont(Font.font(12));
            labelBox.getChildren().add(instrumentLabel);
        }

        ComboBox<String> presetComboBox = new ComboBox<>();
        presetComboBox.getItems().addAll("Preset 1", "Preset 2", "Preset 3", "Preset 4", "Preset 5");
        presetComboBox.setValue("Preset 1");
        presetComboBox.setOnAction(e -> {
            int selectedIndex = presetComboBox.getSelectionModel().getSelectedIndex();
            midiController.setPreset(selectedIndex);
        });

        VBox pianoPresets = createSyntComboBox();
        labelBox.getChildren().add(presetComboBox);
        labelBox.getChildren().add(pianoPresets);

        GridPane mainGrid = new GridPane();
        mainGrid.setVgap(4);
        mainGrid.setHgap(2);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 16; j++) {
                ToggleButton button = new ToggleButton();
                button.setPrefSize(20, 27);
                button.setMinSize(20, 27);
                button.setMaxSize(20, 27);
                if (j % 4 == 0) {
                    button.setStyle("-fx-base: #695859;");
                } else {
                    button.setStyle("-fx-base: #52585C;");
                }
                int finalJ = j;
                button.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        button.setStyle("-fx-base: #f25d52;");
                    } else {
                        if (finalJ % 4 == 0) {
                            button.setStyle("-fx-base: #695859;");
                        } else {
                            button.setStyle("-fx-base: #52585C;");
                        }
                    }
                });
                mainGrid.add(button, j, i);
                buttonList.add(button);
            }
        }

        VBox gridBox = new VBox(mainGrid);
        gridBox.setPadding(new Insets(10));
        gridBox.setStyle("-fx-background-color: #222222; -fx-border-color: #222222;");
        combinedBox = new HBox(10, labelBox, gridBox, pianoScrollPane);
        combinedBox.setPadding(new Insets(10));
        combinedBox.setFillHeight(false);
        combinedBox.setMaxHeight(400);
        combinedBox.setMinWidth(1500);
        combinedBox.setMaxWidth(1600);
        combinedBox.setStyle("-fx-background-color: #222222; -fx-border-color: #61696D;");
    }

    // returns the state of the toggle buttons of the drum machine as a boolean array
    public boolean[] getButtonState() {
        boolean[] buttonState = new boolean[128];
        for (int i = 0; 128 > i; i++) {
            buttonState[i] = buttonList.get(i).isSelected();
        }
        return buttonState;
    }

    //presets dropdown for the synth
    private VBox createSyntComboBox() {
        VBox pianoBox = new VBox(14);
        pianoBox.setPadding(new Insets(12));
        pianoBox.setStyle("-fx-background-color: #222222; -fx-border-color: #222222;");
        ComboBox<String> instrumentComboBox = new ComboBox<>();
        instrumentComboBox.getItems().addAll(
                "Acoustic Grand Piano", "Bright Acoustic Piano", "Electric Grand Piano", "Honky-tonk Piano",
                "Electric Piano 1", "Electric Piano 2", "Harpsichord", "Clavinet",
                "Acoustic Bass", "Electric Bass (finger)", "Electric Bass (pick)", "Fretless Bass", "Slap Bass 1",
                "Square Lead", "Saw Lead", "Calliope Lead", "Chiff Lead", "Charang Lead"
        );
        instrumentComboBox.setValue("Acoustic Grand Piano");
        instrumentComboBox.setOnAction(e -> {
            int selectedIndex = instrumentComboBox.getSelectionModel().getSelectedIndex();
            pianoRoll.setInstrument(pianoRoll.getInstrumentPresets()[selectedIndex]);
        });
        pianoBox.getChildren().add(instrumentComboBox);
        pianoBox.getChildren().add(pianoScrollPane);
        return pianoBox;
    }
}