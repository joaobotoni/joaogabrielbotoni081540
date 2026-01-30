package com.botoni.backend.infra.exceptions;

public class ArtistDomainException extends RuntimeException {
    public ArtistDomainException(String message) {
        super(message);
    }
}
