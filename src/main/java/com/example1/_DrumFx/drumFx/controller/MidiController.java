package com.example1._DrumFx.drumFx.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MidiController {

    public MidiController() {

    }

    @GetMapping("/midiPlayer")
    public String getMidiPlayer(){
        return "midiPlayer";
    }


}
