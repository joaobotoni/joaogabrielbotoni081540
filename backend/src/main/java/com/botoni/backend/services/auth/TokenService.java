package com.botoni.backend.services.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.botoni.backend.dtos.token.RefreshTokenResponse;
import com.botoni.backend.entities.RefreshToken;
import com.botoni.backend.entities.User;
import com.botoni.backend.infra.exceptions.AuthenticationException;
import com.botoni.backend.infra.exceptions.TokenException;
import com.botoni.backend.repositories.RefreshTokenRepository;
import com.botoni.backend.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {
    private static final String TRY_AGAIN = "error.jwt.try-again";
    private static final String LOGIN_AGAIN = "error.jwt.login-again";
    private static final String SESSION_EXPIRED = "error.jwt.session-expired";
    @Value("${jwt.secret}")
    private String secret;
    @Value("${api.domain}")
    private String issuer;
    @Value("${jwt.expiration.access}")
    private Long access;
    @Value("${jwt.expiration.refresh}")
    private Long refresh;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final MessageSource messageSource;
    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer(issuer)
                    .withSubject(user.getEmail().toLowerCase().trim())
                    .withExpiresAt(expiresAt(access))
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new TokenException(getMessage(TRY_AGAIN));
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    @Transactional
    public String generateRefreshToken(UUID userId) {
        if (userId == null) {
            throw new TokenException(getMessage(LOGIN_AGAIN));
        }

        var user = userRepository.findById(userId).orElseThrow(() ->
                new AuthenticationException(getMessage(LOGIN_AGAIN)));

        refreshTokenRepository.deleteByUser(user);

        var token = RefreshToken.builder()
                .user(user)
                .expiresAt(expiresAt(refresh))
                .token(UUID.randomUUID().toString())
                .build();

        refreshTokenRepository.save(token);
        return token.getToken();
    }

    @Transactional
    public RefreshTokenResponse refreshToken(String token) {
        var refreshToken = refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() -> new TokenException(getMessage(LOGIN_AGAIN)));

        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new TokenException(getMessage(SESSION_EXPIRED));
        }

        refreshTokenRepository.delete(refreshToken);
        String newAccessToken = generateToken(refreshToken.getUser());
        String newRefreshToken = generateRefreshToken(refreshToken.getUser().getId());
        return new RefreshTokenResponse(newAccessToken, newRefreshToken);
    }

    private Instant expiresAt(long seconds) {
        return Instant.now().plusSeconds(seconds);
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}