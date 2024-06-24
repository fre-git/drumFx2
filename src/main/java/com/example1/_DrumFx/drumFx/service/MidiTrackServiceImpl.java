package com.example1._DrumFx.drumFx.service;

import com.example1._DrumFx.drumFx.dto.MidiTrackDto;
import com.example1._DrumFx.drumFx.dto.UserDto;
import com.example1._DrumFx.drumFx.model.MidiTrack;
import com.example1._DrumFx.drumFx.model.TrackLike;
import com.example1._DrumFx.drumFx.model.User;
import com.example1._DrumFx.drumFx.repository.MidiTrackRepository;
import com.example1._DrumFx.drumFx.repository.TrackLikeRepository;
import com.example1._DrumFx.drumFx.repository.UserRepository;
import com.example1._DrumFx.drumFx.util.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class MidiTrackServiceImpl implements MidiTrackService{
    @Autowired
    private MidiTrackRepository midiTrackRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrackLikeRepository trackLikeRepository;

    @Override
    public void saveTrack(MultipartFile file, UserDto user) {
        if (!file.isEmpty()) {
            try {
                MidiTrackDto track = new MidiTrackDto();
                track.setFileData(file.getBytes());
                track.setTitle(file.getOriginalFilename());
                track.setUser(user);
                track.setUploadDate(LocalDate.now());
                track.setPopularity(0);

                midiTrackRepository.save(DtoMapper.mapToMidiTrackModel(track));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<MidiTrackDto> findByUser(UserDto user) {
        return DtoMapper.mapToMidiTrackDtoList(midiTrackRepository.findByUser(DtoMapper.mapToUserModel(user)));
    }

    @Override
    public MidiTrackDto findById(Long id) {
        MidiTrack track = midiTrackRepository.findById(id).orElseThrow(() -> new RuntimeException("Track not found"));
        return DtoMapper.mapToMidiTrackDto(track);
    }

    @Override
    public void likeTrack(Long trackId, String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        MidiTrack track = midiTrackRepository.findById(trackId).orElseThrow();

        if (!trackLikeRepository.existsByUserAndTrack(user, track)) {
            track.setLikes(track.getLikes() + 1);
            midiTrackRepository.save(track);
            TrackLike trackLike = new TrackLike();
            trackLike.setUser(user);
            trackLike.setTrack(track);
            trackLikeRepository.save(trackLike);
        }
    }

    @Override
    public List<MidiTrackDto> findNewestTracks(int amount) {
        List<MidiTrack> midiTrackDtoList = midiTrackRepository.findTop10ByOrderByUploadDateDesc();
        return DtoMapper.mapToMidiTrackDtoList(midiTrackDtoList);
    }

    @Override
    public List<MidiTrackDto> findMostPopularTracks(int limit) {
        return DtoMapper.mapToMidiTrackDtoList(midiTrackRepository.findTop10ByOrderByLikesDesc());
    }

    @Override
    public List<MidiTrackDto> findTop10TracksLastMonth() {
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        return DtoMapper.mapToMidiTrackDtoList(midiTrackRepository.findTop10ByOrderByLikesDesc());
    }

}
