package com.example1._DrumFx.drumFx.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MidiTrackDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable=false)
    private byte[] fileData;

    @Column(nullable=false)
    private String title;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserDto user;

    @Column
    private int popularity;

    @Column
    private int likes;

    @Column
    private LocalDate uploadDate;
}
