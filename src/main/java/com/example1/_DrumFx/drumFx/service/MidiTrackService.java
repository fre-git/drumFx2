package com.example1._DrumFx.drumFx.service;

import com.example1._DrumFx.drumFx.dto.MidiTrackDto;
import com.example1._DrumFx.drumFx.dto.UserDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MidiTrackService {
    void saveTrack(MultipartFile file, UserDto user);

    List<MidiTrackDto> findByUser(UserDto user);

    MidiTrackDto findById(Long id);

    List<MidiTrackDto> findNewestTracks(int amount);

    void likeTrack(Long trackId, String userEmail);

    List<MidiTrackDto> findMostPopularTracks(int limit);

    List<MidiTrackDto> findTop10TracksLastMonth();
}
