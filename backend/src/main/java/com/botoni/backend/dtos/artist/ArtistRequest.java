package com.botoni.backend.dtos.artist;

import com.botoni.backend.entities.Genre;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record ArtistRequest(
        @NotNull(message = "O nome é obrigatório.") String name,
        @NotNull(message = "O gênero é obrigatório.") Integer genre
) {
}
