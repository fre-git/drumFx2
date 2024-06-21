package com.example1._DrumFx.drumFx.fxApplication.finalEstForm.view;

import com.example1._DrumFx.drumFx.fxApplication.finalEstForm.AudioApp;
import com.example1._DrumFx.drumFx.fxApplication.finalEstForm.controller.MidiManager;
//import com.example1._DrumFx.drumFx.fxApplication.finalEstForm.model.ArrangementSection;
import com.example1._DrumFx.drumFx.fxApplication.finalEstForm.model.PianoRoll;
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
    private final MidiManager midiManager;
    private final ScrollPane pianoScrollPane;
    private final List<ToggleButton> buttonList = new ArrayList<>();
    private final String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acoustic Snare", "Crash Cymbal", "Hand Clap", "High Tom", "Low-Mid Tom"};
    private HBox combinedBox;
    private PianoRollEditor pianoRollEditor;
    private PianoRoll pianoRoll;

    public DAWGui(AudioApp app, BorderPane borderPane, MidiManager midiManager, ScrollPane pianoScrollPane, PianoRollEditor pianoRollEditor, PianoRoll pianoRoll) {
        this.app = app;
        this.borderPane = borderPane;
        this.midiManager = midiManager;
        this.pianoScrollPane = pianoScrollPane;
        pianoScrollPane.setVvalue(1);
        this.pianoRollEditor = pianoRollEditor;
        this.pianoRoll = pianoRoll;

        createControlButtons();
        createGridWithLabels();
        borderPane.setBottom(combinedBox);
    }

    private void createControlButtons() {
        HBox buttonBox = new HBox();
        buttonBox.setSpacing(120);
        buttonBox.setPadding(new Insets(10));

        Button startButton = new Button();
        Text textStart = new Text("\u25B6");
        textStart.setFont(Font.font(36));
        textStart.setFill(Color.GREEN);
        startButton.setGraphic(textStart);
        startButton.setMinSize(50, 50);
        startButton.setOnAction(e -> {
            midiManager.buildTrack(getButtonState());
            midiManager.startSequencer();
            pianoRollEditor.play();
        });
        buttonBox.getChildren().add(startButton);

        Button stopButton = new Button();
        Text textStop = new Text("\u23F9");
        textStop.setFill(Color.RED);
        textStop.setFont(Font.font(24));
        stopButton.setGraphic(textStop);
        stopButton.setMinSize(50, 50);
        stopButton.setOnAction(e -> {
            midiManager.stop();
            pianoRollEditor.stop();
        });
        buttonBox.getChildren().add(stopButton);

        Button upTempoButton = new Button("Tempo Up");
        upTempoButton.setMinSize(80, 50);
        upTempoButton.setOnAction(e -> app.getTempoController().changeTempo(1));
        buttonBox.getChildren().add(upTempoButton);

        Button downTempoButton = new Button("Tempo Down");
        downTempoButton.setMinSize(80, 50);
        downTempoButton.setOnAction(e -> app.getTempoController().changeTempo(-1));
        buttonBox.getChildren().add(downTempoButton);

        Button saveButton = new Button("Save");
        saveButton.setMinSize(80, 50);
        saveButton.setOnAction(e -> app.saveMidiFile());
        buttonBox.getChildren().add(saveButton);

        borderPane.setTop(buttonBox);
    }

    public void createGridWithLabels() {
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
            pianoRoll.setInstrument(pianoRoll.getInstrumentPresets()[selectedIndex]);
        });
        labelBox.getChildren().add(presetComboBox);

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
        combinedBox = new HBox(10, labelBox, gridBox, createSyntComboBox(), pianoScrollPane);
        combinedBox.setPadding(new Insets(10));
        combinedBox.setFillHeight(false);
        combinedBox.setMaxHeight(350);
        combinedBox.setMinWidth(1500);
        combinedBox.setMaxWidth(1600);
        combinedBox.setStyle("-fx-background-color: #222222; -fx-border-color: #61696D;");
    }

    public boolean[] getButtonState() {
        boolean[] buttonState = new boolean[128];
        for (int i = 0; i < 128; i++) {
            buttonState[i] = buttonList.get(i).isSelected();
        }
        return buttonState;
    }

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
