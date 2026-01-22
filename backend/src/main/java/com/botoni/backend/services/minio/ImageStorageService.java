package com.botoni.backend.services.minio;

import com.botoni.backend.dtos.storage.ImageUploadResponse;
import com.botoni.backend.infra.exceptions.ImageStorageException;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageStorageService {
    private static final String IMAGE_PREFIX = "image/";
    @Value("${minio.bucket-name}")
    private String bucket;

    private final MinioClient minio;

    public ImageUploadResponse upload(MultipartFile file) {
        validate(file);
        String name = uniqueName(file.getOriginalFilename());
        save(file, name);
        return new ImageUploadResponse(bucket, name);
    }

    private void validate(MultipartFile file) {
        checkNotEmpty(file);
        checkIsImage(file.getContentType());
        checkFileName(file.getOriginalFilename());
    }

    private void checkNotEmpty(MultipartFile file) {
        if (isEmpty(file)) throw emptyFile();
    }

    private void checkIsImage(String type) {
        if (!isImage(type)) throw notImage();
    }

    private void checkFileName(String name) {
        if (isInvalidName(name)) throw invalidName();
    }

    private boolean isEmpty(MultipartFile file) {
        return file == null || file.isEmpty();
    }

    private boolean isImage(String type) {
        return type != null && type.startsWith(IMAGE_PREFIX);
    }

    private boolean isInvalidName(String name) {
        return name == null || name.isBlank();
    }

    private void save(MultipartFile file, String name) {
        try {
            minio.putObject(buildPutObjectArgs(file, name));
        } catch (Exception e) {
            throw uploadError(e);
        }
    }

    private PutObjectArgs buildPutObjectArgs(MultipartFile file, String name) {
        try {
            return PutObjectArgs.builder()
                    .bucket(bucket)
                    .contentType(file.getContentType())
                    .object(name)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .build();
        } catch (Exception e) {
            throw uploadError(e);
        }
    }

    private String uniqueName(String original) {
        return UUID.randomUUID() + extension(original);
    }

    private String extension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot > 0 ? fileName.substring(dot) : "";
    }

    private ImageStorageException emptyFile() {
        return new ImageStorageException("Arquivo vazio");
    }

    private ImageStorageException notImage() {
        return new ImageStorageException("Arquivo não é imagem");
    }

    private ImageStorageException invalidName() {
        return new ImageStorageException("Nome inválido");
    }

    private ImageStorageException uploadError(Exception e) {
        return new ImageStorageException("Erro no upload", e);
    }
}