package com.botoni.backend.dtos.authentication;

public record RegisterResponse(
        String username,
        String email,
        String token
) {
}
