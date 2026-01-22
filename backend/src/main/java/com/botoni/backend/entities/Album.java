package com.botoni.backend.entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "artist")
public class Album {

    @Id
    @Column(name = "id_album")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title",  nullable = false)
    private String title;

}
