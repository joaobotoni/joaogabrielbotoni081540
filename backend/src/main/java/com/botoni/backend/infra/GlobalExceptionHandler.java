package com.botoni.backend.infra;

import com.botoni.backend.infra.exceptions.AuthenticationException;
import com.botoni.backend.infra.exceptions.ImageStorageException;
import com.botoni.backend.infra.exceptions.TokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ImageStorageException.class)
    public ProblemDetail handleImageStorageException(ImageStorageException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthenticationException(AuthenticationException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(TokenException.class)
    public ProblemDetail handleTokenException(TokenException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ProblemDetail usernameNotFoundException(UsernameNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Erro de validação");
        var errors = ex.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, e ->
                Objects.requireNonNullElse(e.getDefaultMessage(), "Erro de validação"), (a, b) -> a));
        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(Exception ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor.");
    }
}