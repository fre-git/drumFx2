package com.example1._DrumFx.drumFx.controller;

import com.example1._DrumFx.drumFx.dto.MidiTrackDto;
import com.example1._DrumFx.drumFx.service.MidiTrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/midi")
public class MidiController {

    @Autowired
    MidiTrackService midiTrackService;



    @GetMapping("/{id}.mid")
    public ResponseEntity<Resource> getMidiFile(@PathVariable Long id) {
        MidiTrackDto track = midiTrackService.findById(id);
        ByteArrayResource resource = new ByteArrayResource(track.getFileData());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + track.getTitle() + "\"")
                .body(resource);
    }


}
