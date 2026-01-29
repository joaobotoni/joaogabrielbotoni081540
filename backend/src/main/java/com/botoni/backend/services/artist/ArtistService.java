package com.botoni.backend.services.artist;

import com.botoni.backend.dtos.artist.ArtistRequest;
import com.botoni.backend.dtos.artist.ArtistResponse;
import com.botoni.backend.entities.Artist;
import com.botoni.backend.repositories.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class ArtistService {
    private final ArtistRepository artistRepository;

    public ArtistResponse findByName(String name) {
        return response(artistRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Artist not found!")));
    }

    public Page<ArtistResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return artistRepository.findAll(pageable).map(this::response);
    }

    public Page<ArtistResponse> findByNameWithSort(String name, int page, int size, String sort) {
        Sort.Direction direction = Sort.Direction.fromString(sort);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "name"));
        return artistRepository.findByNameContainingIgnoreCase(name, pageable).map(this::response);
    }

    public ArtistResponse save(ArtistRequest request) {
        Artist artist = Artist.builder()
                .name(request.name())
                .albums(Collections.emptyList())
                .build();
        return response(artistRepository.save(artist));
    }

    private ArtistResponse response(Artist artist) {
        return new ArtistResponse(artist.getId(), artist.getName());
    }
}