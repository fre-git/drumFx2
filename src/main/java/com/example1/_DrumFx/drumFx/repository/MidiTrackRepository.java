package com.example1._DrumFx.drumFx.repository;

import com.example1._DrumFx.drumFx.model.MidiTrack;
import com.example1._DrumFx.drumFx.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MidiTrackRepository extends JpaRepository<MidiTrack, Long> {
    List<MidiTrack> findTop10ByOrderByUploadDateDesc();
    List<MidiTrack> findTop10ByOrderByLikesDesc();

    List<MidiTrack> findByUser(User user);

    @Query("SELECT t FROM MidiTrack t WHERE t.uploadDate >= :lastMonth ORDER BY t.likes DESC")
    List<MidiTrack> findTop10ByLikesLastMonth(@Param("lastMonth") LocalDate lastMonth);
}
