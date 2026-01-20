package com.botoni.backend.dtos.authentication;

public record LoginResponse(
        String username,
        String email,
        String token
) {
}
