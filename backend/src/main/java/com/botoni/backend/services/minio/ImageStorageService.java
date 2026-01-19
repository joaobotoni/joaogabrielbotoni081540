package com.botoni.backend.services.minio;

import com.botoni.backend.dtos.storage.ImageUploadResponse;
import com.botoni.backend.infra.exceptions.ImageStorageException;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageStorageService {
    private static final String FILE_EMPTY = "error.storage.file-empty";
    private static final String FILE_NOT_IMAGE = "error.storage.file-not-image";
    private static final String FILE_INVALID_NAME = "error.storage.file-invalid-name";
    private static final String UPLOAD_ERROR = "error.storage.upload-failed";
    private static final String IMAGE_CONTENT_TYPE_PREFIX = "image/";
    @Value("${minio.bucket-name}")
    private String bucketName;

    private final MinioClient minioClient;
    private final MessageSource messageSource;

    public ImageUploadResponse upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ImageStorageException(getMessage(FILE_EMPTY));
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith(IMAGE_CONTENT_TYPE_PREFIX)) {
            throw new ImageStorageException(getMessage(FILE_NOT_IMAGE));
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isBlank()) {
            throw new ImageStorageException(getMessage(FILE_INVALID_NAME));
        }

        String uniqueFileName = generateUniqueFileName(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .contentType(file.getContentType())
                            .object(uniqueFileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .build()
            );
            return new ImageUploadResponse(bucketName, uniqueFileName);
        } catch (Exception e) {
            throw new ImageStorageException(getMessage(UPLOAD_ERROR), e);
        }
    }

    private String generateUniqueFileName(String originalFileName) {
        int lastDotIndex = originalFileName.lastIndexOf('.');
        String extension = lastDotIndex > 0 ? originalFileName.substring(lastDotIndex) : "";
        return UUID.randomUUID() + extension;
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}