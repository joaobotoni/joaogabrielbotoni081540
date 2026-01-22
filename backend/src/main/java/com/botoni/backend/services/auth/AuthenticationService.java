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
        checkUsername(request.username());
        checkEmail(request.email());
        User user = save(buildUser(request));
        tokenService.addCookie(response, tokenService.generateRefreshToken(user));
        return new AuthenticationResponse(user.getUsername(), user.getEmail());
    }

    public AuthenticationResponse login(LoginRequest request, HttpServletResponse response) {
        User user = findByEmail(request.email());
        checkPassword(request.password(), user);
        tokenService.addCookie(response, tokenService.generateRefreshToken(user));
        return new AuthenticationResponse(user.getUsername(), user.getEmail());
    }

    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String username) {
        return findByEmail(username);
    }

    private void checkEmail(String email) {
        if (isExistsEmail(email)) throw emailExists();
    }

    private void checkPassword(String raw, User user) {
        if (!matches(raw, user.getPassword())) throw invalidCredentials();
    }

    private void checkUsername(String username) {
        if (isExistsUsername(username)) throw usernameExists();
    }

    private boolean isExistsEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
    private boolean isExistsUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(this::invalidCredentials);
    }

    private User save(User user) {
        return userRepository.save(user);
    }

    private User buildUser(RegisterRequest request) {
        return User.builder()
                .username(request.username())
                .email(request.email())
                .password(encode(request.password()))
                .build();
    }

    private String encode(String raw) {
        return passwordEncoder.encode(raw);
    }

    private boolean matches(String raw, String encoded) {
        return passwordEncoder.matches(raw, encoded);
    }

    private AuthenticationException invalidCredentials() {
        return new AuthenticationException("Credenciais inválidas.");
    }

    private AuthenticationException emailExists() {
        return new AuthenticationException("E-mail já cadastrado.");
    }

    private AuthenticationException usernameExists() {
        return new AuthenticationException("Nome de usuário já cadastrado.");
    }
}