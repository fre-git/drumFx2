package com.example1._DrumFx.drumFx.aFxApplication.finalForm.controller;

import com.example1._DrumFx.drumFx.aFxApplication.finalForm.model.PianoRoll;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TempoController {
    private MidiController midiManager;
    private PianoRoll pianoRoll;

    public TempoController(MidiController midiManager, PianoRoll pianoRoll) {
        this.midiManager = midiManager;
        this.pianoRoll = pianoRoll;
    }

    public void changeTempo(int tempoChange) {
        midiManager.changeTempo(tempoChange);
        pianoRoll.changeTempo(tempoChange);
    }

}
