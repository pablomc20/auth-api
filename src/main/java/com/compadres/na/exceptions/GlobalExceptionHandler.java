package com.compadres.na.exceptions;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.compadres.na.dto.ErrorResponse;
import com.compadres.na.exceptions.custom.BaseCustomException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BaseCustomException.class)
    public ResponseEntity<ErrorResponse> handleBaseCustomException(BaseCustomException ex) {    
        logger.error("Excepción personalizada: {} - {}", ex.getError(), ex.getDescription(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getError(),
                ex.getDescription(),
                ex.getStatusCode());

        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String description = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ErrorResponse response = ErrorResponse.builder()
                .error("VALIDATION_ERROR")
                .description(description)
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleSpringAuthenticationException(AuthenticationException ex) {
        ErrorResponse response = ErrorResponse.builder()
                .error("AUTHENTICATION_ERROR")
                .description("Credenciales inválidas")
                .status(HttpStatus.UNAUTHORIZED.value())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedExceptions(Exception ex) {
        ErrorResponse response = ErrorResponse.builder()
                .error("INTERNAL_SERVER_ERROR")
                .description("Ocurrio un error inesperado")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
