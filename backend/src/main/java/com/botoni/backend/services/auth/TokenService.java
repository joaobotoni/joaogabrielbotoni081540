package com.botoni.backend.services.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
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

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${api.domain}")
    private String issuer;

    @Value("${jwt.expiration.access}")
    private Integer accessTokenExpirationInSeconds;

    @Value("${jwt.expiration.refresh}")
    private Integer refreshTokenExpirationInSeconds;

    private final UserRepository userRepository;

    public String generateAccessToken(User user) {
        return createToken(user, accessTokenExpirationInSeconds);
    }

    public String generateRefreshToken(User user) {
        return createToken(user, refreshTokenExpirationInSeconds);
    }

    public String validateToken(String token) {
        try {
            return extractEmailFromToken(token);
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    public void refresh(String token, HttpServletResponse response) {
        if(token == null) throw throwInvalidTokenException();
        User user = extractUserFromToken(token);
        String newAccessToken = generateAccessToken(user);
        String newRefreshToken = generateRefreshToken(user);
        attachRefreshTokenCookie(response, newRefreshToken);
        response.setHeader("Authorization", "Bearer " + newAccessToken);
    }

    public void logout(HttpServletResponse response) {
        clearRefreshTokenCookie(response);
    }

    public void attachRefreshTokenCookie(HttpServletResponse response, String token) {
        response.addCookie(buildRefreshTokenCookie(token));
    }

    private String createToken(User user, long expirationInSeconds) {
        try {
            return buildJwtToken(user, expirationInSeconds);
        } catch (JWTCreationException e) {
            throw throwTokenCreationException();
        }
    }

    private String buildJwtToken(User user, long expirationInSeconds) {
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(normalizeEmail(user.getEmail()))
                .withExpiresAt(calculateExpirationDate(expirationInSeconds))
                .sign(getAlgorithm());
    }

    private String extractEmailFromToken(String token) {
        return verifyAndDecodeToken(token).getSubject();
    }

    private DecodedJWT verifyAndDecodeToken(String token) {
        return JWT.require(getAlgorithm())
                .withIssuer(issuer)
                .build()
                .verify(token);
    }

    private User extractUserFromToken(String token) {
        try {
            String email = extractEmailFromToken(token);
            return findUserByEmail(email);
        } catch (JWTVerificationException e) {
            throw throwInvalidTokenException();
        }
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(this::throwUserNotFoundException);
    }

    private Cookie buildRefreshTokenCookie(String value) {
        return createCookie(value, refreshTokenExpirationInSeconds);
    }

    private Cookie buildExpiredCookie() {
        return createCookie("", 0);
    }

    private Cookie createCookie(String value, Integer maxAgeInSeconds) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, value);
        applyCookieSecuritySettings(cookie);
        cookie.setMaxAge(maxAgeInSeconds);
        return cookie;
    }

    private void applyCookieSecuritySettings(Cookie cookie) {
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setAttribute("SameSite", "Strict");
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        response.addCookie(buildExpiredCookie());
    }

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secret);
    }

    private Instant calculateExpirationDate(long seconds) {
        return Instant.now().plusSeconds(seconds);
    }

    private String normalizeEmail(String email) {
        return email.toLowerCase().trim();
    }

    private TokenException throwTokenCreationException() {
        return new TokenException("Erro ao gerar token.");
    }

    private TokenException throwInvalidTokenException() {
        return new TokenException("Token inválido ou expirado.");
    }

    private AuthenticationException throwUserNotFoundException() {
        return new AuthenticationException("Usuário não encontrado.");
    }
}