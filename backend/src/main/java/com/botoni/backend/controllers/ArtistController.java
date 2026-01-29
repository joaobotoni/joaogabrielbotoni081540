package com.botoni.backend.controllers;

import com.botoni.backend.dtos.artist.ArtistRequest;
import com.botoni.backend.dtos.artist.ArtistResponse;
import com.botoni.backend.services.artist.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/artist")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;

    @PostMapping("/create")
    public ResponseEntity<ArtistResponse> create(@RequestBody ArtistRequest request) {
        return ResponseEntity.ok(artistService.save(request));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<ArtistResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(artistService.findAll(page, size));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ArtistResponse>> search(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "ASC") String sort) {
        return ResponseEntity.ok(artistService.findByNameWithSort(name, page, size, sort));
    }
}