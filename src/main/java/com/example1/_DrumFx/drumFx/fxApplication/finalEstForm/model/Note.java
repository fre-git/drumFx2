package com.example1._DrumFx.drumFx.fxApplication.finalEstForm.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Note {
    private double x;
    private double y;
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