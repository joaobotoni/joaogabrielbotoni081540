package com.botoni.backend.services.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
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
    private Integer accessExpiration;

    @Value("${jwt.expiration.refresh}")
    private Integer refreshExpiration;

    private final UserRepository userRepository;

    public String generateAccessToken(User user) {
        return createToken(user, accessExpiration);
    }

    public String generateRefreshToken(User user) {
        return createToken(user, refreshExpiration);
    }

    public String validateToken(String token) {
        try {
            return extractEmail(token);
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    public TokenResponse refresh(String token, HttpServletResponse response) {
        User user = extractUser(token);
        addCookie(response, generateRefreshToken(user));
        return new TokenResponse(generateAccessToken(user));
    }

    public void logout(HttpServletResponse response) {
        removeCookie(response);
    }

    public void addCookie(HttpServletResponse response, String token) {
        response.addCookie(buildCookie(token));
    }

    private String createToken(User user, long exp) {
        try {
            return buildJwt(user, exp);
        } catch (JWTCreationException e) {
            throw tokenCreationError();
        }
    }

    private String buildJwt(User user, long exp) {
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(normalizeEmail(user.getEmail()))
                .withExpiresAt(expirationAt(exp))
                .sign(getAlgorithm());
    }

    private String extractEmail(String token) {
        return verifyToken(token).getSubject();
    }

    private DecodedJWT verifyToken(String token) {
        return JWT.require(getAlgorithm())
                .withIssuer(issuer)
                .build()
                .verify(token);
    }

    private User extractUser(String token) {
        try {
            return findUser(extractEmail(token));
        } catch (JWTVerificationException e) {
            throw invalidToken();
        }
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(this::userNotFound);
    }

    private Cookie buildCookie(String value) {
        return createCookie(value, refreshExpiration);
    }

    private Cookie buildExpiredCookie() {
        return createCookie("", 0);
    }

    private Cookie createCookie(String value, Integer max) {
        Cookie cookie = new Cookie("refreshToken", value);
        configureSecurity(cookie);
        cookie.setMaxAge(max);
        return cookie;
    }

    private void configureSecurity(Cookie cookie) {
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
    }

    private void removeCookie(HttpServletResponse response) {
        response.addCookie(buildExpiredCookie());
    }

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secret);
    }

    private Instant expirationAt(long seconds) {
        return Instant.now().plusSeconds(seconds);
    }

    private String normalizeEmail(String email) {
        return email.toLowerCase().trim();
    }

    private TokenException tokenCreationError() {
        return new TokenException("Erro ao gerar token.");
    }

    private TokenException invalidToken() {
        return new TokenException("Token inválido ou expirado.");
    }

    private AuthenticationException userNotFound() {
        return new AuthenticationException("Usuário não encontrado.");
    }
}