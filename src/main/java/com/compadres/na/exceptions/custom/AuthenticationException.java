package com.compadres.na.exceptions.custom;

import org.springframework.http.HttpStatus;

/**
 * Excepción personalizada para errores de autenticación y autorización.
 * Se utiliza cuando hay problemas con credenciales, tokens JWT o permisos de acceso.
 */
public class AuthenticationException extends BaseCustomException {

    public AuthenticationException(String description) {
        super("AUTHENTICATION_ERROR", description, HttpStatus.UNAUTHORIZED);
    }

    public AuthenticationException(String description, Throwable cause) {
        super("AUTHENTICATION_ERROR", description, HttpStatus.UNAUTHORIZED, cause);
    }

    public static AuthenticationException invalidCredentials() {
        return new AuthenticationException("Credenciales inválidas");
    }

    public static AuthenticationException userNotFound(String username) {
        return new AuthenticationException("Usuario '" + username + "' no encontrado");
    }

    public static AuthenticationException tokenExpired() {
        return new AuthenticationException("El token JWT ha expirado");
    }

    public static AuthenticationException invalidToken() {
        return new AuthenticationException("Token JWT inválido");
    }

    public static AuthenticationException accessDenied() {
        return new AuthenticationException("Acceso denegado: no tienes permisos suficientes");
    }

    public static AuthenticationException accountDisabled() {
        return new AuthenticationException("La cuenta de usuario está deshabilitada");
    }
}