package com.compadres.na.exceptions.custom;

import org.springframework.http.HttpStatus;

public class OAuthException extends BaseCustomException {

    public OAuthException(String error, String description, HttpStatus status) {
        super(error, description, status);
    }

    public static OAuthException invalidAuthorizationRequest(String description) {
        return new OAuthException("INVALID_REQUEST", description, HttpStatus.BAD_REQUEST);
    }

    public static OAuthException invalidClient() {
        return new OAuthException("INVALID_CLIENT", "Credenciales de cliente inválidas", HttpStatus.UNAUTHORIZED);
    }

    public static OAuthException invalidGrant(String description) {
        return new OAuthException("INVALID_GRANT", description, HttpStatus.BAD_REQUEST);
    }

    public static OAuthException unsupportedGrantType() {
        return new OAuthException("UNSUPPORTED_GRANT_TYPE", "El grant_type no es soportado", HttpStatus.BAD_REQUEST);
    }
}
