package com.botoni.backend.dtos.artist;

import com.botoni.backend.entities.Genre;

import java.util.UUID;

public record ArtistResponse(UUID id, String name, Genre genre) {
}
