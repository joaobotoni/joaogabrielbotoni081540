package com.botoni.backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "album")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Album {

    @Id
    @Column(name = "id_album")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title",  nullable = false)
    private String title;

    @ManyToMany(mappedBy = "albums")
    private List<Artist> artists;
}
