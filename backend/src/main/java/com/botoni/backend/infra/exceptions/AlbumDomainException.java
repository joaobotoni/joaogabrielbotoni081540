package com.botoni.backend.infra.exceptions;

public class AlbumDomainException extends RuntimeException {
    public AlbumDomainException(String message) {
        super(message);
    }
}
