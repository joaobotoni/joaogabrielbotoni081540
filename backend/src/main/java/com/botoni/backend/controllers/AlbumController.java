package com.botoni.backend.controllers;

import com.botoni.backend.dtos.album.AlbumRequest;
import com.botoni.backend.dtos.album.AlbumResponse;
import com.botoni.backend.services.album.AlbumService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/albums")
public class AlbumController {

    private final AlbumService albumService;

    @GetMapping("/search")
    public ResponseEntity<AlbumResponse> findByName(@RequestParam String name) {
        return ResponseEntity.ok(albumService.findByTitle(name));
    }

    @GetMapping
    public ResponseEntity<Page<AlbumResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String direction) {
        return ResponseEntity.ok(albumService.findAll(page, size, sortBy, direction));
    }

    @PostMapping("/create")
    public ResponseEntity<AlbumResponse> create(@RequestBody AlbumRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(albumService.create(request));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<AlbumResponse> update(@PathVariable UUID id, @RequestBody AlbumRequest request) {
        return ResponseEntity.ok(albumService.update(id, request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        albumService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
