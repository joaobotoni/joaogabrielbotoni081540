package com.botoni.backend.entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

public class Artist {
    @Id
    @Column(name = "id_artist")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String nome;
}
