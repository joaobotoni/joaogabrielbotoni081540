package com.botoni.backend.services.album;

import com.botoni.backend.dtos.album.AlbumRequest;
import com.botoni.backend.dtos.album.AlbumResponse;
import com.botoni.backend.entities.Album;
import com.botoni.backend.infra.exceptions.AlbumDomainException;
import com.botoni.backend.repositories.AlbumRepository;
import com.botoni.backend.uitils.mapper.AlbumMapper;
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
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;

    public AlbumResponse findByTitle(String title) {
        return albumRepository.findByTitle(title)
                .map(albumMapper::map)
                .orElseThrow(() -> new AlbumDomainException("Álbum não encontrado."));
    }

    public AlbumResponse findById(UUID id) {
        return albumRepository.findById(id)
                .map(albumMapper::map)
                .orElseThrow(() -> new AlbumDomainException("Álbum não encontrado."));
    }

    @Transactional
    public Page<AlbumResponse> findAll(int page, int size, String sortBy, String direction) {
        Pageable pageable = (sortBy != null && direction != null)
                ? PageRequest.of(page, size, Sort.by(Sort.Direction.valueOf(direction), sortBy))
                : PageRequest.of(page, size);

        return albumRepository.findAll(pageable).map(albumMapper::map);
    }

    @Transactional
    public AlbumResponse create(AlbumRequest request) {
        validateUniqueTitle(request.title());

        Album album = albumMapper.map(request);

        return albumMapper.map(albumRepository.save(album));
    }

    @Transactional
    public AlbumResponse update(UUID id, AlbumRequest request) {
        Album existingAlbum = albumRepository.findById(id)
                .orElseThrow(() -> new AlbumDomainException("Álbum não encontrado."));

        if (!existingAlbum.getTitle().equalsIgnoreCase(request.title())) {
            validateUniqueTitle(request.title());
        }

        albumMapper.map(existingAlbum, request);

        return albumMapper.map(albumRepository.save(existingAlbum));
    }

    @Transactional
    public void delete(UUID id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new AlbumDomainException("Álbum não encontrado."));
        albumRepository.delete(album);
    }

    private void validateUniqueTitle(String title) {
        if (albumRepository.findByTitle(title).isPresent()) {
            throw new AlbumDomainException("Este álbum já está cadastrado.");
        }
    }
}