package com.example1._DrumFx.drumFx.fxApplication.FinalForm;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
    private final BorderPane borderPane;
    private final MidiManager midiManager;
    private final ScrollPane pianoRollScrollPane;
    private final List<ToggleButton> buttonList = new ArrayList<>();
    private final String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acoustic Snare", "Crash Cymbal", "Hand Clap", "High Tom", "Low-Mid Tom"};
    private HBox combinedBox;
    private PianoRollEditor pianoRollEditor;

    public DAWGui(BorderPane borderPane, MidiManager midiManager, ScrollPane pianoRollScrollPane, PianoRollEditor pianoRollEditor) {
        this.borderPane = borderPane;
        this.midiManager = midiManager;
        this.pianoRollScrollPane = pianoRollScrollPane;
        this.pianoRollEditor = pianoRollEditor;
        createControlButtons();
        createGridWithLabels();
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
            midiManager.buildTrackAndStart(getButtonState());
            ((PianoRollEditor) pianoRollScrollPane.getContent()).play();
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
            ((PianoRollEditor) pianoRollScrollPane.getContent()).stop();
        });
        buttonBox.getChildren().add(stopButton);

        Button upTempoButton = new Button("Tempo Up");
        upTempoButton.setMinSize(80, 50);
        upTempoButton.setOnAction(e -> {
            midiManager.changeTempo(1.03f);
            pianoRollEditor.changeTempo(1.03f);
        });
        buttonBox.getChildren().add(upTempoButton);

        Button downTempoButton = new Button("Tempo Down");
        downTempoButton.setMinSize(80, 50);
        downTempoButton.setOnAction(e -> {
            midiManager.changeTempo(0.97f);
            pianoRollEditor.changeTempo(0.97f);
        });
        buttonBox.getChildren().add(downTempoButton);


//        Button upTempoButton = new Button("Tempo Up");
//        upTempoButton.setMinSize(80, 50);
//        upTempoButton.setOnAction(e -> {
//            midiManager.changeTempo(1.03f);
//            ((PianoRollEditor) pianoRollScrollPane.getContent()).changeTempo(1.03f);
//        });
//        buttonBox.getChildren().add(upTempoButton);
//
//        Button downTempoButton = new Button("Tempo Down");
//        downTempoButton.setMinSize(80, 50);
//        downTempoButton.setOnAction(e -> {
//            midiManager.changeTempo(0.97f);
//            ((PianoRollEditor) pianoRollScrollPane.getContent()).changeTempo(0.97f);
//        });
//        buttonBox.getChildren().add(downTempoButton);

        borderPane.setTop(buttonBox);
    }

    public void createGridWithLabels() {
        VBox labelBox = new VBox(10);
        labelBox.setPadding(new Insets(8));
        labelBox.setStyle("-fx-background-color: #222222; -fx-border-color: #222222;");
        for (String instrumentName : instrumentNames) {
            Label instrumentLabel = new Label(instrumentName);
            instrumentLabel.setTextFill(Color.WHITE);
            instrumentLabel.setFont(Font.font(14));
            labelBox.getChildren().add(instrumentLabel);
        }

        GridPane mainGrid = new GridPane();
        mainGrid.setVgap(8);
        mainGrid.setHgap(10);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 16; j++) {
                ToggleButton button = new ToggleButton();
                button.setPrefSize(15, 21);
                button.setMinSize(15, 21);
                button.setMaxSize(15, 21);
                if (j % 4 == 0) {
                    button.setStyle("-fx-base: #695859;");
                } else {
                    button.setStyle("-fx-base: #52585C;");
                }
                int finalJ = j;
                button.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        button.setStyle("-fx-base: #FE4032;");
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

        combinedBox = new HBox(10, labelBox, gridBox, pianoRollScrollPane);
        combinedBox.setPadding(new Insets(10));
        combinedBox.setFillHeight(false);
        combinedBox.setMinWidth(570);
        combinedBox.setMaxWidth(1200); // Ensure enough width for the PianoRollEditor
        combinedBox.setStyle("-fx-background-color: #222222; -fx-border-color: #61696D;");
    }

    public HBox getCombinedBox() {
        return combinedBox;
    }

    public boolean[] getButtonState() {
        boolean[] buttonState = new boolean[128];
        for (int i = 0; 128 > i; i++) {
            buttonState[i] = buttonList.get(i).isSelected();
        }
        return buttonState;
    }
}
