package com.botoni.backend.dtos.authentication;

public record LoginRequest(
        String email,
        String password
) {
}
