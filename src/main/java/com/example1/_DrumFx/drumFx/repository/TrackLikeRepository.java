package com.example1._DrumFx.drumFx.repository;

import com.example1._DrumFx.drumFx.model.MidiTrack;
import com.example1._DrumFx.drumFx.model.TrackLike;
import com.example1._DrumFx.drumFx.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackLikeRepository extends JpaRepository<TrackLike, Long> {
    boolean existsByUserAndTrack(User user, MidiTrack track);
}
