package com.botoni.backend.services.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.botoni.backend.dtos.token.TokenResponse;
import com.botoni.backend.entities.User;
import com.botoni.backend.infra.exceptions.AuthenticationException;
import com.botoni.backend.infra.exceptions.TokenException;
import com.botoni.backend.repositories.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TokenService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${api.domain}")
    private String issuer;

    @Value("${jwt.expiration.access}")
    private Long accessExpiration;

    @Value("${jwt.expiration.refresh}")
    private Long refreshExpiration;

    private final UserRepository userRepository;

    public String generateAccessToken(User user) {
        return createToken(user, accessExpiration);
    }

    public String generateRefreshToken(User user) {
        return createToken(user, refreshExpiration);
    }

    public String validateToken(String token) {
        try {
            return JWT.require(getAlgorithm())
                    .withIssuer(issuer)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    public TokenResponse refreshToken(String token) {
        try {
            String email = JWT.require(getAlgorithm())
                    .withIssuer(issuer)
                    .build()
                    .verify(token)
                    .getSubject();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AuthenticationException("Usuário não encontrado."));

            return new TokenResponse(
                    generateAccessToken(user),
                    generateRefreshToken(user)
            );
        } catch (JWTVerificationException e) {
            throw new TokenException("Refresh token inválido ou expirado.");
        }
    }

    public void addRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(refreshExpiration.intValue());
        response.addCookie(cookie);
    }

    public void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private String createToken(User user, long expirationSeconds) {
        try {
            return JWT.create()
                    .withIssuer(issuer)
                    .withSubject(user.getEmail().toLowerCase().trim())
                    .withExpiresAt(getExpirationInstant(expirationSeconds))
                    .sign(getAlgorithm());
        } catch (JWTCreationException e) {
            throw new TokenException("Erro ao gerar token.");
        }
    }

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secret);
    }

    private Instant getExpirationInstant(long seconds) {
        return Instant.now().plusSeconds(seconds);
    }
}