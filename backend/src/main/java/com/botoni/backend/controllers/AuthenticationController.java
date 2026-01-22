package com.botoni.backend.controllers;

import com.botoni.backend.dtos.authentication.AuthenticationResponse;
import com.botoni.backend.dtos.authentication.LoginRequest;
import com.botoni.backend.dtos.authentication.RegisterRequest;
import com.botoni.backend.services.auth.AuthenticationService;
import com.botoni.backend.services.auth.TokenService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;
    private final TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody @Valid RegisterRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(authService.register(request, response));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid LoginRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(authService.login(request, response));
    }

    @PutMapping("/refresh")
    public ResponseEntity<Void> refresh(@CookieValue("refreshToken") String token, HttpServletResponse response) {
        if(token == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        tokenService.refresh(token, response);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        tokenService.logout(response);
        return ResponseEntity.noContent().build();
    }
}