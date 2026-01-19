package com.botoni.backend.services.auth;

import com.botoni.backend.dtos.authentication.LoginResponse;
import com.botoni.backend.dtos.authentication.RegisterRequest;
import com.botoni.backend.dtos.authentication.LoginRequest;
import com.botoni.backend.dtos.authentication.RegisterResponse;
import com.botoni.backend.entities.User;
import com.botoni.backend.infra.exceptions.AuthenticationException;
import com.botoni.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements UserDetailsService {
    private static final String EMAIL_NOT_FOUND = "error.authentication.email-not-found";
    private static final String EMAIL_ALREADY_EXISTS = "error.authentication.email-already-exists";
    private static final String INVALID_CREDENTIALS = "error.authentication.invalid-credentials";
    private static final String USER_NOT_FOUND = "error.authentication.user-not-found";

    private final UserRepository userRepository;

    private final TokenService tokenService;

    private final PasswordEncoder passwordEncoder;

    private final MessageSource messageSource;

    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(getMessage(USER_NOT_FOUND)));
    }

    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new AuthenticationException(getMessage(EMAIL_ALREADY_EXISTS));
        }

        var newUser = User.builder()
                .name(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        var savedUser = userRepository.save(newUser);

        String token = tokenService.generateToken(savedUser);
        String refreshToken = tokenService.generateRefreshToken(savedUser.getId());

        return new RegisterResponse(
                savedUser.getName(),
                savedUser.getEmail(),
                token,
                refreshToken
        );
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AuthenticationException(getMessage(EMAIL_NOT_FOUND)));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new AuthenticationException(getMessage(INVALID_CREDENTIALS));
        }

        String token = tokenService.generateToken(user);
        String refreshToken = tokenService.generateRefreshToken(user.getId());

        return new LoginResponse(
                user.getName(),
                user.getEmail(),
                token,
                refreshToken
        );
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}