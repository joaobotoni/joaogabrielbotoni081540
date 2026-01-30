package com.botoni.backend.dtos.album;

import java.time.Year;
import java.util.UUID;

public record AlbumResponse(UUID id, String title, Year year) {
}
