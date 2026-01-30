package com.botoni.backend.services.auth;
import com.botoni.backend.dtos.authentication.AuthenticationResponse;
import com.botoni.backend.dtos.authentication.LoginRequest;
import com.botoni.backend.dtos.authentication.RegisterRequest;
import com.botoni.backend.entities.User;
import com.botoni.backend.infra.exceptions.AuthenticationException;
import com.botoni.backend.repositories.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
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
        User user = create(request);
        User savedUser = save(user);
        return buildAuthenticationResponse(savedUser, response);
    }

    public AuthenticationResponse login(LoginRequest request, HttpServletResponse response) {
        User user = findByEmail(request.email());
        validatePassword(request.password(), user);
        return buildAuthenticationResponse(user, response);
    }

    @Override

    public @NonNull UserDetails loadUserByUsername(@NonNull String username) {
        return findByEmail(username);
    }

    private AuthenticationResponse buildAuthenticationResponse(User user, HttpServletResponse response) {
        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);
        tokenService.addCookie(response, refreshToken);
        return new AuthenticationResponse(user.getAlias(), user.getEmail(), accessToken);
    }

    private void validateUsername(String username) {
        if (checkUsername(username)) throw usernameExists();
    }


    private void validateEmail(String email) {
        if (checkEmail(email)) throw emailExists();
    }


    private void validatePassword(String raw, User user) {
        if (!matches(raw, user.getPassword())) throw invalidCredentials();
    }


    private boolean checkEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }


    private boolean checkUsername(String username) {
        return userRepository.findByAlias(username).isPresent();
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(this::invalidCredentials);
    }

    private User create(RegisterRequest request) {
        return User.builder()
                .alias(request.username())
                .email(request.email())
                .password(encode(request.password()))
                .build();
    }

    private User save(User user) {
        return userRepository.save(user);
    }

    private String encode(String raw) {
        return passwordEncoder.encode(raw);
    }

    private boolean matches(String raw, String encoded) {
        return passwordEncoder.matches(raw, encoded);
    }

    private AuthenticationException invalidCredentials() {
        return new AuthenticationException("E-mail ou senha incorretos.");
    }


    private AuthenticationException emailExists() {
        return new AuthenticationException("Este endereço de e-mail já está em uso.");
    }

    private AuthenticationException usernameExists() {
        return new AuthenticationException("Este nome de usuário já está sendo utilizado.");
    }
}