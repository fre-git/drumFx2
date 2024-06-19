package com.example1._DrumFx.drumFx.fxApplication.drum;

import com.example1._DrumFx.drumFx.fxApplication.pianoRollNew.PianoRoll;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Gui {

    private final BorderPane borderPane;
    private final MidiHandler midiHandler;
    private final List<ToggleButton> buttonList = new ArrayList<>();
    private final String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acoustic Snare", "Crash Cymbal",
            "Hand Clap", "High Tom", "Low-Mid Tom"};

    private final PianoRoll pianoRoll;

    public Gui(BorderPane borderPane, MidiHandler midiHandler, PianoRoll pianoRoll) {
        this.borderPane = borderPane;
        this.midiHandler = midiHandler;
        this.pianoRoll = pianoRoll;
        createMenuBar();
        createControlButtons();
    }

    private void createMenuBar() {
        MenuBar menuBar = new MenuBar();
        VBox vBox = new VBox(menuBar);

        MenuItem save = new MenuItem("Save file");
        save.setOnAction(e -> saveSequenceToFile());

        MenuItem load = new MenuItem("Load file");
        load.setOnAction(e -> loadSequenceFromFile());

        MenuItem export = new MenuItem("Export to MIDI");
        export.setOnAction(e -> saveMidiToFile());

        Menu menu = new Menu("File");
        menu.getItems().addAll(save, load, export);
        menuBar.getMenus().add(menu);

        borderPane.setTop(vBox);
    }

    private void createControlButtons() {
        HBox buttonBox = new HBox();
        buttonBox.setSpacing(120);
        buttonBox.setPadding(new Insets(10));

        Button playButton = new Button();
        Text textStart = new Text("\u25B6");
        textStart.setFont(Font.font(36));
        textStart.setFill(Color.GREEN);
        playButton.setGraphic(textStart);
        playButton.setMinSize(50, 50);
        playButton.setOnAction(e -> {
            midiHandler.buildTrackAndStart(getButtonState());
            pianoRoll.play();
        });


//        playButton.setOnAction(e -> midiHandler.buildTrackAndStart(getButtonState()));
        buttonBox.getChildren().add(playButton);

        Button stopButton = new Button();
        Text textStop = new Text("\u23F9");
        textStop.setFill(Color.RED);
        textStop.setFont(Font.font(24));
        stopButton.setGraphic(textStop);
        stopButton.setMinSize(50, 50);

        stopButton.setOnAction(e -> {
            midiHandler.stop();
//            midiHandler.getSequencer().stop();
            pianoRoll.stop();
        });

//        stopButton.setOnAction(e -> midiHandler.getSequencer().stop());
        buttonBox.getChildren().add(stopButton);

        Button upTempoButton = new Button("Tempo Up");
        upTempoButton.setMinSize(80, 50);

        upTempoButton.setOnAction(e -> {
            midiHandler.changeTempo(1.03f);
            pianoRoll.changeTempo(1.03f);
        });

//        upTempoButton.setOnAction(e -> midiHandler.changeTempo(1.03f));
        buttonBox.getChildren().add(upTempoButton);

        Button downTempoButton = new Button("Tempo Down");
        downTempoButton.setMinSize(80, 50);

        downTempoButton.setOnAction(e -> {
            midiHandler.changeTempo(0.97f);
            pianoRoll.changeTempo(0.97f);
        });

//        downTempoButton.setOnAction(e -> midiHandler.changeTempo(0.97f));
        buttonBox.getChildren().add(downTempoButton);

        borderPane.setTop(buttonBox);
    }

    public HBox createDrumBox() {
        VBox labelBox = new VBox(10);
        labelBox.setPadding(new Insets(8));
        labelBox.setStyle("-fx-background-color: #222222; -fx-border-color: #222222;"); // Set background color and border
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
        gridBox.setStyle("-fx-background-color: #222222; -fx-border-color: #222222; ");

        HBox combinedBox = new HBox(10, labelBox, gridBox);
        combinedBox.setPadding(new Insets(10));
        combinedBox.setFillHeight(false);
        combinedBox.setMinWidth(570);
        combinedBox.setMaxWidth(570);
        combinedBox.setStyle("-fx-background-color: #222222; -fx-border-color: #61696D; ");
        return combinedBox;
    }

    public boolean[] getButtonState() {
        boolean[] buttonState = new boolean[128];
        for (int i = 0; i < 128; i++) {
            buttonState[i] = buttonList.get(i).isSelected();
        }
        return buttonState;
    }

    private void saveSequenceToFile() {
        boolean[] buttonState = getButtonState();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Drum Sequence");
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            midiHandler.saveSequenceToFile(buttonState, file);
        }
    }

    private void loadSequenceFromFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Drum Sequence");
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            boolean[] buttonState = midiHandler.loadSequenceFromFile(file);

            if (buttonState != null && buttonState.length == 128) {
                for (int i = 0; i < 128; i++) {
                    buttonList.get(i).setSelected(buttonState[i]);
                }
            }

            midiHandler.buildTrackAndStart(buttonState);
        }
    }

    private void saveMidiToFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save MIDI File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MIDI files", "*.mid"));

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            midiHandler.exportSequenceToMidiFile(file);
        }
    }









//    private final BorderPane borderPane;
//    private final MidiHandler midiHandler;
//    private final List<ToggleButton> buttonList = new ArrayList<>();
//    private final String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acoustic Snare", "Crash Cymbal",
//            "Hand Clap", "High Tom", "Low-Mid Tom"};
//
//    public Gui(BorderPane borderPane, MidiHandler midiHandler) {
//        this.borderPane = borderPane;
//        this.midiHandler = midiHandler;
//        createMenuBar();
//        createControlButtons();
//        createGridWithLabels();
//    }
//
//    private void createMenuBar() {
//        MenuBar menuBar = new MenuBar();
//        VBox vBox = new VBox(menuBar);
//
//        MenuItem save = new MenuItem("Save file");
//        save.setOnAction(e -> saveSequenceToFile());
//
//        MenuItem load = new MenuItem("Load file");
//        load.setOnAction(e -> loadSequenceFromFile());
//
//        MenuItem export = new MenuItem("Export to MIDI");
//        export.setOnAction(e -> saveMidiToFile());
//
//        Menu menu = new Menu("File");
//        menu.getItems().addAll(save, load, export);
//        menuBar.getMenus().add(menu);
//
//        borderPane.setTop(vBox);
//    }
//
//    private void createControlButtons() {
//        HBox buttonBox = new HBox();
//        buttonBox.setSpacing(120);
//        buttonBox.setPadding(new Insets(10));
//
//        Button startButton = new Button();
//        Text textStart = new Text("\u25B6");
//        textStart.setFont(Font.font(36));
//        textStart.setFill(Color.GREEN);
//        startButton.setGraphic(textStart);
//        startButton.setMinSize(50, 50);
//        startButton.setOnAction(e -> midiHandler.buildTrackAndStart(getButtonState()));
//        buttonBox.getChildren().add(startButton);
//
//        Button stopButton = new Button();
//        Text textStop = new Text("\u23F9");
//        textStop.setFill(Color.RED);
//        textStop.setFont(Font.font(24));
//        stopButton.setGraphic(textStop);
//        stopButton.setMinSize(50, 50);
//        stopButton.setOnAction(e -> midiHandler.getSequencer().stop());
//        buttonBox.getChildren().add(stopButton);
//
//        Button upTempoButton = new Button("Tempo Up");
//        upTempoButton.setMinSize(80, 50);
//        upTempoButton.setOnAction(e -> midiHandler.changeTempo(1.03f));
//        buttonBox.getChildren().add(upTempoButton);
//
//        Button downTempoButton = new Button("Tempo Down");
//        downTempoButton.setMinSize(80, 50);
//        downTempoButton.setOnAction(e -> midiHandler.changeTempo(0.97f));
//        buttonBox.getChildren().add(downTempoButton);
//
//        borderPane.setTop(buttonBox);
//    }
//
//    private void createGridWithLabels() {
//        // Create the VBox for the labels
//        VBox labelBox = new VBox(10);
//        labelBox.setPadding(new Insets(8));
//        labelBox.setStyle("-fx-background-color: #222222; -fx-border-color: #222222;"); // Set background color and border
//        for (String instrumentName : instrumentNames) {
//            Label instrumentLabel = new Label(instrumentName);
//            instrumentLabel.setTextFill(Color.WHITE);
//            instrumentLabel.setFont(Font.font(14));
//            labelBox.getChildren().add(instrumentLabel);
//        }
//
//        // Create the GridPane for the buttons
//        GridPane mainGrid = new GridPane();
//        mainGrid.setVgap(8);
//        mainGrid.setHgap(10);
//
//        for (int i = 0; i < 8; i++) {
//            for (int j = 0; j < 16; j++) {
//                ToggleButton button = new ToggleButton();
//                button.setPrefSize(15, 21);
//                button.setMinSize(15, 21);
//                button.setMaxSize(15, 21);
//                if (j % 4 == 0) {
//                    button.setStyle("-fx-base: #695859;");
//                } else {
//                    button.setStyle("-fx-base: #52585C;");
//                }
//                int finalJ = j;
//                button.selectedProperty().addListener((observable, oldValue, newValue) -> {
//                    if (newValue) {
//                        button.setStyle("-fx-base: #FE4032;");
//                    } else {
//                        if (finalJ % 4 == 0) {
//                            button.setStyle("-fx-base: #695859;");
//                        } else {
//                            button.setStyle("-fx-base: #52585C;");
//                        }
//                    }
//                });
//                mainGrid.add(button, j, i);
//                buttonList.add(button);
//            }
//        }
//
//        // Create a VBox for the grid with border and background
//        VBox gridBox = new VBox(mainGrid);
//        gridBox.setPadding(new Insets(10));
//        gridBox.setStyle("-fx-background-color: #222222; -fx-border-color: #222222; "); // Set background color and border
//
//        // Combine labels and grid in an HBox
//        HBox combinedBox = new HBox(10, labelBox, gridBox);
//        combinedBox.setPadding(new Insets(10));
//        combinedBox.setFillHeight(false);
//        combinedBox.setMinWidth(570);
//        combinedBox.setMaxWidth(570);
//        borderPane.setBottom(combinedBox);
//        combinedBox.setStyle("-fx-background-color: #222222; -fx-border-color: #61696D; ");
////        borderPane.setCenter(combinedBox);
//    }
//
//    private boolean[] getButtonState() {
//        boolean[] buttonState = new boolean[128];
//        for (int i = 0; i < 128; i++) {
//            buttonState[i] = buttonList.get(i).isSelected();
//        }
//        return buttonState;
//    }
//
//    private void saveSequenceToFile() {
//        boolean[] buttonState = getButtonState();
//
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Save Drum Sequence");
//        File file = fileChooser.showSaveDialog(null);
//
//        if (file != null) {
//            midiHandler.saveSequenceToFile(buttonState, file);
//        }
//    }
//
//    private void loadSequenceFromFile() {
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Load Drum Sequence");
//        File file = fileChooser.showOpenDialog(null);
//
//        if (file != null) {
//            boolean[] buttonState = midiHandler.loadSequenceFromFile(file);
//
//            if (buttonState != null && buttonState.length == 128) {
//                for (int i = 0; i < 128; i++) {
//                    buttonList.get(i).setSelected(buttonState[i]);
//                }
//            }
//
//            midiHandler.buildTrackAndStart(buttonState);
//        }
//    }
//
//    private void saveMidiToFile() {
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Save MIDI File");
//        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MIDI files", "*.mid"));
//
//        File file = fileChooser.showSaveDialog(null);
//        if (file != null) {
//            midiHandler.exportSequenceToMidiFile(file);
//        }
//    }
}
