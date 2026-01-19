package com.botoni.backend.dtos.authentication;

public record RegisterRequest(
        String username,
        String email,
        String password
) {
}
