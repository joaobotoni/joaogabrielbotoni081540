package com.botoni.backend.services.artist;

import com.botoni.backend.dtos.artist.ArtistRequest;
import com.botoni.backend.dtos.artist.ArtistResponse;
import com.botoni.backend.entities.Artist;
import com.botoni.backend.entities.Genre;
import com.botoni.backend.infra.exceptions.ArtistDomainException;
import com.botoni.backend.repositories.ArtistRepository;
import com.botoni.backend.repositories.GenreRepository;
import com.botoni.backend.uitils.mapper.ArtistMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final GenreRepository genreRepository;
    private final ArtistMapper artistMapper;

    public ArtistResponse findByName(String name) {
        return artistRepository.findByName(name)
                .map(artistMapper::map)
                .orElseThrow(() -> new ArtistDomainException("Artista não encontrado."));
    }

    public ArtistResponse findById(UUID id) {
        return artistRepository.findById(id)
                .map(artistMapper::map)
                .orElseThrow(() -> new ArtistDomainException("Artista não encontrado."));
    }

    @Transactional
    public Page<ArtistResponse> findAll(int page, int size, String sortBy, String direction) {
        Pageable pageable = (sortBy != null && direction != null)
                ? PageRequest.of(page, size, Sort.by(Sort.Direction.valueOf(direction), sortBy))
                : PageRequest.of(page, size);

        return artistRepository.findAll(pageable).map(artistMapper::map);
    }

    @Transactional
    public ArtistResponse create(ArtistRequest request) {
        validateUniqueName(request.name());

        Genre genre = genreRepository.findById(request.genre())
                .orElseThrow(() -> new ArtistDomainException("Gênero não encontrado."));

        Artist artist = artistMapper.map(request);
        artist.setGenre(genre);

        return artistMapper.map(artistRepository.save(artist));
    }

    @Transactional
    public ArtistResponse update(UUID id, ArtistRequest request) {
        Artist existingArtist = artistRepository.findById(id)
                .orElseThrow(() -> new ArtistDomainException("Artista não encontrado."));

        if (!existingArtist.getName().equalsIgnoreCase(request.name())) {
            validateUniqueName(request.name());
        }

        Genre genre = genreRepository.findById(request.genre())
                .orElseThrow(() -> new ArtistDomainException("Gênero não encontrado."));

        artistMapper.map(existingArtist, request);
        existingArtist.setGenre(genre);

        return artistMapper.map(artistRepository.save(existingArtist));
    }

    @Transactional
    public void delete(UUID id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ArtistDomainException("Artista não encontrado."));

        artistRepository.delete(artist);
    }

    private void validateUniqueName(String name) {
        if (artistRepository.findByName(name).isPresent()) {
            throw new ArtistDomainException("Este artista já está cadastrado.");
        }
    }
}