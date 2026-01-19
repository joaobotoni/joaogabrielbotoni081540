package com.botoni.backend.controllers;

import com.botoni.backend.dtos.authentication.LoginRequest;
import com.botoni.backend.dtos.authentication.LoginResponse;
import com.botoni.backend.dtos.authentication.RegisterRequest;
import com.botoni.backend.dtos.authentication.RegisterResponse;
import com.botoni.backend.dtos.token.TokenResponse;
import com.botoni.backend.services.auth.AuthenticationService;
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
        RegisterResponse data = authenticationService.register(request);
        tokenService.addRefreshTokenToCookie(response, data.refreshToken());
        return ResponseEntity.ok(data);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request, HttpServletResponse response) {
        LoginResponse data = authenticationService.login(request);
        tokenService.addRefreshTokenToCookie(response, data.refreshToken());
        return ResponseEntity.ok(data);
    }

    @PutMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {
        TokenResponse tokens = tokenService.refreshToken(refreshToken);
        tokenService.addRefreshTokenToCookie(response, tokens.refreshToken());
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        tokenService.clearRefreshTokenCookie(response);
        return ResponseEntity.noContent().build();
    }
}