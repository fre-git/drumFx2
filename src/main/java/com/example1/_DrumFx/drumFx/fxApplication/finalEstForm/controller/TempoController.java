package com.example1._DrumFx.drumFx.fxApplication.finalEstForm.controller;

import com.example1._DrumFx.drumFx.fxApplication.finalEstForm.model.PianoRoll;
import com.example1._DrumFx.drumFx.fxApplication.finalEstForm.view.PianoRollEditor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TempoController {
    private MidiManager midiManager;
    private PianoRoll pianoRoll;

    public TempoController(MidiManager midiManager, PianoRoll pianoRoll) {
        this.midiManager = midiManager;
        this.pianoRoll = pianoRoll;
    }

    public void changeTempo(int tempoChange) {
        midiManager.changeTempo(tempoChange);
        pianoRoll.changeTempo(tempoChange);
        System.out.println(midiManager.getSequencer().getTempoInBPM());
        System.out.println(pianoRoll.getSequencer().getTempoInBPM());
    }

    public void setTempo(int bpm) {
        midiManager.setTempo(bpm);
        pianoRoll.setTempo(bpm);
    }
}
