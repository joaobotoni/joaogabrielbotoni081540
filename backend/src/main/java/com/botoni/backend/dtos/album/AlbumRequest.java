package com.botoni.backend.dtos.album;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NonNull;

import java.time.Year;

public record AlbumRequest(
        @Valid
        @NotNull(message = "O titulo do album não pode ser nulo") String title,
        @NotNull(message = "O ano de lançamento não pode ser nulo") Year year
) {
}
