//package com.example1._DrumFx.drumFx.fxApplication.finalEstForm.model;
//
//import com.example1._DrumFx.drumFx.fxApplication.finalEstForm.controller.MidiManager;
//import com.example1._DrumFx.drumFx.fxApplication.finalEstForm.view.PianoRollEditor;
//import javafx.scene.paint.Color;
//import javafx.scene.shape.Rectangle;
//import lombok.Getter;
//import lombok.Setter;
//
//@Getter
//@Setter
//public class ArrangementSection {
//    private double x, y;
//    private double width, height;
//    private Rectangle rectangle;
//    private String type;
//    private String pianoRollState;
//    private double duration; // Duration in seconds
//    private double startTime; // Start time in seconds
//
//    public ArrangementSection() {
//        this(0, 0, 200, 50, Color.GRAY, "Unknown", null, 0, 0);
//    }
//
//    public ArrangementSection(double x, double y, double width, double height, Color color, String type, String pianoRollState, double duration, double startTime) {
//        this.x = x;
//        this.y = y;
//        this.width = width;
//        this.height = height;
//        this.rectangle = new Rectangle(x, y, width, height);
//        this.rectangle.setFill(color);
//        this.type = type;
//        this.pianoRollState = pianoRollState;
//        this.duration = duration;
//        this.startTime = startTime;
//    }
//
//    public void preparePlayback(MidiManager midiManager, PianoRollEditor pianoRollEditor) {
//        if ("PianoRoll".equals(type) && pianoRollState != null) {
//            pianoRollEditor.loadState(pianoRollState);
//        }
//    }
//
//    public void play(MidiManager midiManager, PianoRollEditor pianoRollEditor) {
//        if ("PianoRoll".equals(type)) {
//            pianoRollEditor.play();
//        }
//    }
//
//    public void stop(MidiManager midiManager, PianoRollEditor pianoRollEditor) {
//        if ("PianoRoll".equals(type)) {
//            pianoRollEditor.stop();
//        }
//    }
//}
//
