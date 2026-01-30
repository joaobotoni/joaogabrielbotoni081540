package com.botoni.backend.services.auth;

import com.botoni.backend.dtos.authentication.AuthenticationResponse;
import com.botoni.backend.dtos.authentication.LoginRequest;
import com.botoni.backend.dtos.authentication.RegisterRequest;
import com.botoni.backend.entities.User;
import com.botoni.backend.infra.exceptions.AuthenticationException;
import com.botoni.backend.repositories.UserRepository;
import com.botoni.backend.uitils.mapper.AuthMapper;
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
    private final AuthMapper authMapper;
    private final TokenService tokenService;

    public AuthenticationResponse register(RegisterRequest request, HttpServletResponse response) {
        validateUniqueUsername(request.username());
        validateUniqueEmail(request.email());

        User user = authMapper.map(request);
        user.setPassword(passwordEncoder.encode(request.password()));

        return generateAuthenticationResponse(userRepository.save(user), response);
    }

    public AuthenticationResponse login(LoginRequest request, HttpServletResponse response) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AuthenticationException("E-mail ou senha incorretos."));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new AuthenticationException("E-mail ou senha incorretos.");
        }

        return generateAuthenticationResponse(user, response);
    }

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new AuthenticationException("E-mail ou senha incorretos."));
    }

    private AuthenticationResponse generateAuthenticationResponse(User user, HttpServletResponse response) {
        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);
        tokenService.addRefreshTokenCookie(response, refreshToken);
        return authMapper.map(user, accessToken);
    }

    private void validateUniqueUsername(String username) {
        if (userRepository.findByAlias(username).isPresent()) {
            throw new AuthenticationException("Este nome de usuário já está sendo utilizado.");
        }
    }

    private void validateUniqueEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new AuthenticationException("Este endereço de e-mail já está em uso.");
        }
    }
}