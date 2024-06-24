package com.example1._DrumFx.drumFx.controller;

import com.example1._DrumFx.drumFx.dto.MidiTrackDto;
import com.example1._DrumFx.drumFx.service.MidiTrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private MidiTrackService midiTrackService;


    @GetMapping("/home")
    public String showHomePage(Model model) {
        List<MidiTrackDto> topTracksLastMonth = midiTrackService.findTop10TracksLastMonth();
        model.addAttribute("topTracksLastMonth", topTracksLastMonth);
        return "home";
    }


    @GetMapping("/topTenAllTime")
    public String showtopTenAllTime(Model model) {
        List<MidiTrackDto> mostPopularTracks = midiTrackService.findMostPopularTracks(10);
        model.addAttribute("mostPopularTracks", mostPopularTracks);
        return "topTenAllTime";
    }


    @GetMapping("/newTracks")
    public String showNewestTracks(Model model) {
        List<MidiTrackDto> newestTracks = midiTrackService.findNewestTracks(10);
        model.addAttribute("newestTracks", newestTracks);
        return "newTracks";
    }
}
