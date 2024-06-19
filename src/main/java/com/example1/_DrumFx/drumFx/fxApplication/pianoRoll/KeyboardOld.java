package com.example1._DrumFx.drumFx.fxApplication.pianoRoll;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class KeyboardOld extends Canvas {
    private static final int WHITE_KEY_HEIGHT = 20;
    private static final int BLACK_KEY_HEIGHT = 14;
    private static final int WHITE_KEY_WIDTH = 30;
    private static final int BLACK_KEY_WIDTH = 20;
    private static final int TOTAL_KEYS = 88; // Number of keys in a standard piano

    public KeyboardOld(double height) {
        super(WHITE_KEY_WIDTH, height);
        drawKeyboard();
    }

    private void drawKeyboard() {

        GraphicsContext gc = getGraphicsContext2D();
        int numberOfWhiteKeys = TOTAL_KEYS;

//        int numberOfWhiteKeys = (int)(getHeight() / WHITE_KEY_HEIGHT);

        for (int i = 0; i < numberOfWhiteKeys; i++) {
            int midiPitch = 21 + i; // A0 starts at MIDI pitch 21
            double whiteKeyY = i * WHITE_KEY_HEIGHT;

            // Draw white keys
//            gc.setFill(Color.WHITE);
//            gc.fillRect(0, whiteKeyY, WHITE_KEY_WIDTH, WHITE_KEY_HEIGHT);
            gc.setStroke(Color.BLACK);
            gc.strokeRect(0, whiteKeyY, WHITE_KEY_WIDTH, WHITE_KEY_HEIGHT);

            // Draw black keys
            if (i % 7 != 2 && i % 7 != 6) { // Skip E and B keys
                gc.setFill(Color.BLACK);
                double blackKeyY = whiteKeyY + (WHITE_KEY_HEIGHT - (BLACK_KEY_HEIGHT - BLACK_KEY_HEIGHT/2));
                gc.fillRect(0, blackKeyY, BLACK_KEY_WIDTH, BLACK_KEY_HEIGHT);
            }
        }
    }
















//    private static final int WHITE_KEY_HEIGHT = 10;
//    private static final int BLACK_KEY_HEIGHT = 7;
//    private static final int WHITE_KEY_WIDTH = 30;
//    private static final int BLACK_KEY_WIDTH = 20;
//    private static final int TOTAL_KEYS = 88; // Number of keys in a standard piano
//
//    public Keyboard(double height) {
//        super(WHITE_KEY_WIDTH, height);
//        drawKeyboard();
//    }
//
//    private void drawKeyboard() {
//        GraphicsContext gc = getGraphicsContext2D();
//        int numberOfWhiteKeys = (int)(getHeight() / WHITE_KEY_HEIGHT);
//
//        for (int i = 0; i < numberOfWhiteKeys; i++) {
//            int midiPitch = 21 + i; // A0 starts at MIDI pitch 21
//            // Draw white keys
//            gc.setFill(Color.WHITE);
//            gc.fillRect(0, i * WHITE_KEY_HEIGHT, WHITE_KEY_WIDTH, WHITE_KEY_HEIGHT);
//            gc.setStroke(Color.BLACK);
//            gc.strokeRect(0, i * WHITE_KEY_HEIGHT, WHITE_KEY_WIDTH, WHITE_KEY_HEIGHT);
//
//            // Draw black keys
//            if (i % 7 != 2 && i % 7 != 6) { // Skip E and B keys
//                gc.setFill(Color.BLACK);
//                gc.fillRect(0, i * WHITE_KEY_HEIGHT + (double) (WHITE_KEY_HEIGHT - BLACK_KEY_HEIGHT) / 2, BLACK_KEY_WIDTH, BLACK_KEY_HEIGHT);
//            }
//        }
//    }
}
