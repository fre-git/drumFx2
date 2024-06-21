//package com.example1._DrumFx.drumFx.fxApplication.finalEstForm.view;
//
//import com.example1._DrumFx.drumFx.fxApplication.finalEstForm.controller.MidiManager;
//import com.example1._DrumFx.drumFx.fxApplication.finalEstForm.model.ArrangementSection;
//import com.example1._DrumFx.drumFx.fxApplication.finalEstForm.model.PianoRoll;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import javafx.scene.layout.Pane;
//import javafx.stage.FileChooser;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//@Getter
//@Setter
//public class ArrangementView extends Pane {
//    private List<ArrangementSection> sections;
//    private ObjectMapper objectMapper;
//    private MidiManager midiManager;
//    private PianoRollEditor pianoRollEditor;
//
//    public ArrangementView(MidiManager midiManager, PianoRollEditor pianoRollEditor) {
//        this.sections = new ArrayList<>();
//        this.setStyle("-fx-background-color: #333333;");
//        this.setPrefSize(1600, 400);
//        this.objectMapper = new ObjectMapper();
//        this.midiManager = midiManager;
//        this.pianoRollEditor = pianoRollEditor;
//    }
//
//    public void addSection(ArrangementSection section) {
//        sections.add(section);
//        this.getChildren().add(section.getRectangle());
//    }
//
//    public void removeSection(ArrangementSection section) {
//        sections.remove(section);
//        this.getChildren().remove(section.getRectangle());
//    }
//
//    public void moveSection(ArrangementSection section, double newX, double newY) {
//        section.setX(newX);
//        section.setY(newY);
//    }
//
////    public void saveArrangementToFile() {
////        FileChooser fileChooser = new FileChooser();
////        fileChooser.setTitle("Save Arrangement");
////        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));
////        File file = fileChooser.showSaveDialog(null);
////        if (file != null) {
////            try {
////                objectMapper.writeValue(file, sections);
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////        }
////    }
////
////    public void loadArrangementFromFile() {
////        FileChooser fileChooser = new FileChooser();
////        fileChooser.setTitle("Load Arrangement");
////        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));
////        File file = fileChooser.showOpenDialog(null);
////        if (file != null) {
////            try {
////                List<ArrangementSection> loadedSections = objectMapper.readValue(file, new TypeReference<List<ArrangementSection>>() {});
////                sections.clear();
////                this.getChildren().clear();
////                for (ArrangementSection section : loadedSections) {
////                    addSection(section);
////                    if ("PianoRoll".equals(section.getType())) {
////                        PianoRollModel tempPianoRollModel = new PianoRollModel(midiManager.getSequencer(), midiManager.getSequence());
////                        PianoRollEditor tempPianoRollEditor = new PianoRollEditor(tempPianoRollModel);
////                        tempPianoRollEditor.loadState(section.getPianoRollState());
////                        // Store the temporary piano roll editor in the section or another list if needed
////                    }
////                }
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////        }
////    }
//
//    public void playArrangement() {
//        // Stop any previously running sequences
//        midiManager.stop();
//        pianoRollEditor.stop();
//
//        new Thread(() -> {
//            double currentTime = 0;
//            for (ArrangementSection section : sections) {
//                section.setStartTime(currentTime);
//                section.preparePlayback(midiManager, pianoRollEditor);
//                section.play(midiManager, pianoRollEditor);
//
//                double sectionDuration = section.getDuration();
//                double tempoFactor = 120.0 / midiManager.getTempo(); // Normalize duration based on tempo
//                long playbackDuration = (long) (sectionDuration * tempoFactor * 1000); // Convert to milliseconds
//
//                try {
//                    Thread.sleep(playbackDuration); // Wait for the duration of the section
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                section.stop(midiManager, pianoRollEditor);
//                currentTime += sectionDuration;
//            }
//        }).start();
//    }
//
//    public void stopArrangement() {
//        for (ArrangementSection section : sections) {
//            section.stop(midiManager, pianoRollEditor);
//        }
//    }
//}
