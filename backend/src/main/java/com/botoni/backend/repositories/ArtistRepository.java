package com.botoni.backend.repositories;

import com.botoni.backend.entities.Artist;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArtistRepository extends JpaRepository<Artist, UUID> {
    Optional<Artist> findByName(String name);
    @NonNull Page<Artist> findByNameContainingIgnoreCase(String name, @NonNull Pageable pageable);
}
