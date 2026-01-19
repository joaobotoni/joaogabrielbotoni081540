package com.botoni.backend.controllers;

import com.botoni.backend.dtos.storage.ImageUploadResponse;
import com.botoni.backend.services.minio.ImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/storage")
@RequiredArgsConstructor
public class ImageStorageController {
    private final ImageStorageService imageStorageService;
    @PostMapping("/upload")
    public ResponseEntity<ImageUploadResponse> uploadImage(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(imageStorageService.upload(file));
    }
}
