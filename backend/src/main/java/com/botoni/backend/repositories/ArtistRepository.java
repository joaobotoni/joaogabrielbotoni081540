package com.botoni.backend.repositories;

import com.botoni.backend.entities.Artist;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ArtistRepository extends JpaRepository<Artist, UUID> {
    Optional<Artist> findByName(String name);
    @NonNull Optional<Artist> findById(UUID id);

}
