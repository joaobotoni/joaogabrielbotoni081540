package com.botoni.backend.infra.exceptions;

public class TokenException extends RuntimeException {
    public TokenException(String message) {
        super(message);
    }

    TokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
