package com.example1._DrumFx.drumFx.aFxApplication.finalForm.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Note {
    private double x;  // x position
    private double y;  // y position
    private double width;
    private double height;

    public Note() {
    }

    public Note(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}