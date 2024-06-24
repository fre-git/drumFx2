package com.example1._DrumFx.drumFx.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class MidiTrack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable=false,  columnDefinition = "MEDIUMBLOB")
    private byte[] fileData;

    @Column(nullable=false)
    private String title;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private int popularity;

    @Column(nullable = false)
    private int likes = 0;

    @Column
    private LocalDate uploadDate;
}
