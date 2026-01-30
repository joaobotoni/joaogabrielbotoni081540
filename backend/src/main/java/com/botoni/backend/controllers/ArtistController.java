package com.botoni.backend.controllers;

import com.botoni.backend.dtos.artist.ArtistRequest;
import com.botoni.backend.dtos.artist.ArtistResponse;
import com.botoni.backend.services.artist.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/artists")
@RequiredArgsConstructor
public class ArtistController {
    private final ArtistService artistService;
    @GetMapping("/search")
    public ResponseEntity<ArtistResponse> findByName(@RequestParam String name) {
        return ResponseEntity.ok(artistService.findByName(name));
    }

    @GetMapping
    public ResponseEntity<Page<ArtistResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String direction) {
        return ResponseEntity.ok(artistService.findAll(page, size, sortBy, direction));
    }

    @PostMapping("/create")
    public ResponseEntity<ArtistResponse> create(@RequestBody ArtistRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(artistService.create(request));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ArtistResponse> update(
            @PathVariable UUID id,
            @RequestBody ArtistRequest request) {
        return ResponseEntity.ok(artistService.update(id, request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        artistService.delete(id);
        return ResponseEntity.noContent().build();
    }
}