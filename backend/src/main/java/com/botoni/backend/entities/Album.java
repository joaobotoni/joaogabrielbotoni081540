package com.botoni.backend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Year;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "album")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Album {

    @Id
    @Column(name = "id_album")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title", unique = true,  nullable = false)
    private String title;

    @Column(name = "year_released", nullable = false)
    private Year year;

    @ManyToMany(mappedBy = "albums")
    private List<Artist> artists;
}
