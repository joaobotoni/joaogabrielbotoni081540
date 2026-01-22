package com.botoni.backend.dtos.authentication;

public record AuthenticationResponse(
        String username,
        String email
) {
}
