package com.botoni.backend.dtos.token;

import java.util.UUID;

public record RefreshTokenResponse(String accessToken, String token) {
}
