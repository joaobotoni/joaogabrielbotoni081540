package com.botoni.backend.services.auth;

import com.botoni.backend.dtos.authentication.*;
import com.botoni.backend.entities.User;
import com.botoni.backend.infra.exceptions.AuthenticationException;
import com.botoni.backend.repositories.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthenticationResponse register(RegisterRequest request, HttpServletResponse response) {
        validateUsername(request.username());
        validateEmail(request.email());
        User user = createUser(request);
        User saved = saveUser(user);
        return buildResponse(saved, response);
    }

    public AuthenticationResponse login(LoginRequest request, HttpServletResponse response) {
        User user = findByEmail(request.email());
        validatePassword(request.password(), user);
        return buildResponse(user, response);
    }

    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String username) {
        return findByEmail(username);
    }

    private AuthenticationResponse buildResponse(User user, HttpServletResponse response) {
        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);
        tokenService.attachRefreshTokenCookie(response, refreshToken);
        response.setHeader("Authorization", "Bearer " + accessToken);
        return new AuthenticationResponse(user.getUsername(), user.getEmail());
    }

    private void validateUsername(String username) {
        if (usernameExists(username)) {
            throw usernameExists();
        }
    }

    private void validateEmail(String email) {
        if (emailExists(email)) {
            throw emailExists();
        }
    }

    private void validatePassword(String raw, User user) {
        if (!passwordMatches(raw, user.getPassword())) {
            throw invalidCredentials();
        }
    }

    private boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(this::invalidCredentials);
    }

    private User saveUser(User user) {
        return userRepository.save(user);
    }

    private User createUser(RegisterRequest request) {
        return User.builder()
                .username(request.username())
                .email(request.email())
                .password(encodePassword(request.password()))
                .build();
    }

    private String encodePassword(String raw) {
        return passwordEncoder.encode(raw);
    }

    private boolean passwordMatches(String raw, String encoded) {
        return passwordEncoder.matches(raw, encoded);
    }

    private AuthenticationException invalidCredentials() {return new AuthenticationException("Credenciais inválidas.");}

    private AuthenticationException emailExists() {
        return new AuthenticationException("E-mail já cadastrado.");
    }

    private AuthenticationException usernameExists() {return new AuthenticationException("Nome de usuário já cadastrado.");}
}