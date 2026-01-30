package com.botoni.backend.services.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.botoni.backend.dtos.authentication.AuthenticationResponse;
import com.botoni.backend.entities.User;
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
    private static final String COOKIE_PATH = "/";
    private static final String SAME_SITE_STRICT = "Strict";
    private static final String CLAIM_TYPE = "typ";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${api.domain}")
    private String issuer;

    @Value("${jwt.expiration.access}")
    private Integer accessTokenExpiration;

    @Value("${jwt.expiration.refresh}")
    private Integer refreshTokenExpiration;

    private final UserRepository userRepository;

    public String generateAccessToken(User user) {
        return createToken(user, accessTokenExpiration, TYPE_ACCESS);
    }

    public String generateRefreshToken(User user) {
        return createToken(user, refreshTokenExpiration, TYPE_REFRESH);
    }

    public String validateToken(String token) {
        try {
            return decodeAndVerify(token, TYPE_ACCESS).getSubject();
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    public AuthenticationResponse refresh(String token, HttpServletResponse response) {
        User user = extractUserFromToken(token);
        String newAccessToken = generateAccessToken(user);
        String newRefreshToken = generateRefreshToken(user);

        addRefreshTokenCookie(response, newRefreshToken);

        return new AuthenticationResponse(newAccessToken, user.getAlias(), user.getEmail());
    }

    public void logout(HttpServletResponse response) {
        clearRefreshTokenCookie(response);
    }

    public void addRefreshTokenCookie(HttpServletResponse response, String token) {
        response.addCookie(createCookie(token, refreshTokenExpiration));
    }

    private String createToken(User user, Integer expirationInSeconds, String tokenType) {
        try {
            return JWT.create()
                    .withIssuer(issuer)
                    .withSubject(user.getEmail())
                    .withClaim(CLAIM_TYPE, tokenType)
                    .withExpiresAt(Instant.now().plusSeconds(expirationInSeconds))
                    .sign(Algorithm.HMAC256(secret));
        } catch (JWTCreationException e) {
            throw new TokenException("Erro ao criar token: " + e.getMessage());
        }
    }

    private DecodedJWT decodeAndVerify(String token, String expectedType) {
        try {
            return JWT.require(Algorithm.HMAC256(secret))
                    .withIssuer(issuer)
                    .withClaim(CLAIM_TYPE, expectedType)
                    .build()
                    .verify(token);
        } catch (JWTVerificationException e) {
            throw new TokenException("Token inválido ou expirado: " + e.getMessage());
        }
    }

    private User extractUserFromToken(String token) {
        String email = decodeAndVerify(token, TYPE_REFRESH).getSubject();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new TokenException("Token inválido ou usuário não encontrado"));
    }

    private Cookie createCookie(String value, Integer maxAgeInSeconds) {
        Cookie cookie = new Cookie(COOKIE_NAME, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath(COOKIE_PATH);
        cookie.setAttribute("SameSite", SAME_SITE_STRICT);
        cookie.setMaxAge(maxAgeInSeconds);
        return cookie;
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie expiredCookie = new Cookie(COOKIE_NAME, "");
        expiredCookie.setPath(COOKIE_PATH);
        expiredCookie.setMaxAge(0);
        response.addCookie(expiredCookie);
    }
}