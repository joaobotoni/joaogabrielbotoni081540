// AuthenticationController.java
package com.botoni.backend.controllers;

import com.botoni.backend.dtos.authentication.LoginRequest;
import com.botoni.backend.dtos.authentication.LoginResponse;
import com.botoni.backend.dtos.authentication.RegisterRequest;
import com.botoni.backend.dtos.authentication.RegisterResponse;
import com.botoni.backend.dtos.token.TokenPair;
import com.botoni.backend.dtos.token.TokenResponse;
import com.botoni.backend.services.auth.AuthenticationService;
import com.botoni.backend.services.auth.AuthenticationService.AuthResult;
import com.botoni.backend.services.auth.TokenService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest request, HttpServletResponse response) {
        AuthResult<RegisterResponse> result = authenticationService.register(request);
        tokenService.addRefreshTokenToCookie(response, result.refreshToken());
        return ResponseEntity.ok(result.response());
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request, HttpServletResponse response) {
        AuthResult<LoginResponse> result = authenticationService.login(request);
        tokenService.addRefreshTokenToCookie(response, result.refreshToken());
        return ResponseEntity.ok(result.response());
    }

    @PutMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {
        TokenPair tokens = tokenService.refreshToken(refreshToken);
        tokenService.addRefreshTokenToCookie(response, tokens.refreshToken());
        return ResponseEntity.ok(tokens.response());
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        tokenService.clearRefreshTokenCookie(response);
        return ResponseEntity.ok("Desconectado com sucesso");
    }
}