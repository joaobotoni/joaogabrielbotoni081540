package com.botoni.backend.services.auth;

import com.botoni.backend.dtos.authentication.LoginRequest;
import com.botoni.backend.dtos.authentication.LoginResponse;
import com.botoni.backend.dtos.authentication.RegisterRequest;
import com.botoni.backend.dtos.authentication.RegisterResponse;
import com.botoni.backend.entities.User;
import com.botoni.backend.infra.exceptions.AuthenticationException;
import com.botoni.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements UserDetailsService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public RegisterResponse register(RegisterRequest request) {
        validateEmailNotExists(request.email());
        User newUser = save(request);
        User savedUser = userRepository.save(newUser);
        return buildRegisterResponse(savedUser);
    }

    public LoginResponse login(LoginRequest request) {
        User user = findUserByEmail(request.email());
        validatePassword(request.password(), user.getPassword());
        return buildLoginResponse(user);
    }

    private void validateEmailNotExists(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new AuthenticationException("Este e-mail já está cadastrado. Por favor, faça login ou use outro e-mail.");
        }
    }

    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new AuthenticationException("Credenciais inválidas. Verifique e tente novamente.");
        }
    }

    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findUserByEmail(username);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
    }

    private User save(RegisterRequest request) {
        return User.builder()
                .name(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();
    }

    private RegisterResponse buildRegisterResponse(User user) {
        return new RegisterResponse(
                user.getName(),
                user.getEmail(),
                tokenService.generateAccessToken(user),
                tokenService.generateRefreshToken(user)
        );
    }

    private LoginResponse buildLoginResponse(User user) {
        return new LoginResponse(
                user.getName(),
                user.getEmail(),
                tokenService.generateAccessToken(user),
                tokenService.generateRefreshToken(user)
        );
    }
}