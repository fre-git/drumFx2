package com.example1._DrumFx.drumFx.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example1._DrumFx.drumFx.dto.MidiTrackDto;
import com.example1._DrumFx.drumFx.service.MidiTrackService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MidiController.class)
public class MidiControllerTest {

    @MockBean
    private MidiTrackService midiTrackService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should return MIDI file for a given ID")
    @WithMockUser(username = "user", roles = {"USER"})
    void getMidiFile() throws Exception {
        MidiTrackDto midiTrackDto = new MidiTrackDto();
        midiTrackDto.setId(1L);
        midiTrackDto.setTitle("test.mid");
        midiTrackDto.setFileData("MIDI file content".getBytes());

        when(midiTrackService.findById(anyLong())).thenReturn(midiTrackDto);

        mockMvc.perform(get("/midi/1.mid"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"test.mid\""));
    }
}