package com.botoni.backend.services.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.botoni.backend.dtos.authentication.AuthenticationResponse;
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
    public static final String COOKIE_NAME = "refresh";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String COOKIE_PATH = "/";
    private static final String SAME_SITE_ATTRIBUTE = "SameSite";
    private static final String SAME_SITE_VALUE = "Strict";
    @Value("${jwt.secret}")
    private String secret;

    @Value("${api.domain}")
    private String issuer;

    @Value("${jwt.expiration.access}")
    private Integer access;

    @Value("${jwt.expiration.refresh}")
    private Integer refresh;

    private final UserRepository userRepository;

    public String generateAccessToken(User user) {
        validateUser(user);
        return createToken(user, access);
    }

    public String generateRefreshToken(User user) {
        validateUser(user);
        return createToken(user, refresh);
    }

    public String validateToken(String token) {
        try {
            return getSubject(token);
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    public AuthenticationResponse refresh(String token, HttpServletResponse response) {
        validatePresentToken(token);
        User user = extractUserPayload(token);
        String newAccessToken = generateAccessToken(user);
        String newRefreshToken = generateRefreshToken(user);
        addCookie(response, newRefreshToken);
        response.setHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + newAccessToken);
        return new AuthenticationResponse(user.getAlias(), user.getEmail());
    }

    public void logout(HttpServletResponse response) {
        clearCookie(response);
    }

    public void addCookie(HttpServletResponse response, String token) {
        validatePresentToken(token);
        response.addCookie(buildCookie(token));
    }

    private String createToken(User user, long expirationInSeconds) {
        try {
            return buildJwt(user, expirationInSeconds);
        } catch (JWTCreationException e) {
            throw throwTokenCreationException();
        }
    }

    private String buildJwt(User user, long seconds) {
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(normalize(user.getEmail()))
                .withExpiresAt(expiration(seconds))
                .sign(getAlgorithm());
    }

    private String getSubject(String token) {
        return decode(token).getSubject();
    }

    private DecodedJWT decode(String token) {
        return JWT.require(getAlgorithm())
                .withIssuer(issuer)
                .build()
                .verify(token);
    }

    private User extractUserPayload(String token) {
        try {
            String email = getSubject(token);
            return findByEmail(email);
        } catch (JWTVerificationException e) {
            throw throwAuthenticationException();
        }
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(this::throwAuthenticationException);
    }

    private Cookie buildCookie(String value) {
        return createCookie(value, refresh);
    }

    private Cookie buildExpired() {
        return createCookie("", 0);
    }

    private Cookie createCookie(String value, Integer maxAgeInSeconds) {
        Cookie cookie = new Cookie(COOKIE_NAME, value);
        secure(cookie);
        cookie.setMaxAge(maxAgeInSeconds);
        return cookie;
    }

    private void secure(Cookie cookie) {
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath(COOKIE_PATH);
        cookie.setAttribute(SAME_SITE_ATTRIBUTE, SAME_SITE_VALUE);
    }

    private void clearCookie(HttpServletResponse response) {
        response.addCookie(buildExpired());
    }

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secret);
    }

    private Instant expiration(long seconds) {
        return Instant.now().plusSeconds(seconds);
    }

    private String normalize(String email) {
        return email.toLowerCase().trim();
    }

    private void validateUser(User user) {
        if (user == null) throw throwInvalidDataException();
        if (user.getEmail() == null || user.getEmail().isBlank()) throw throwInvalidDataException();
    }

    private void validatePresentToken(String token) {
        if (token == null || token.isBlank()) throw throwAuthenticationException();
    }

    private TokenException throwTokenCreationException() {
        return new TokenException("Erro no processamento");
    }

    private TokenException throwAuthenticationException() {
        return new TokenException("Sessão inválida. Autenticação necessária.");
    }

    private IllegalArgumentException throwInvalidDataException() {
        return new IllegalArgumentException("Dados de conta incompletos.");
    }
}