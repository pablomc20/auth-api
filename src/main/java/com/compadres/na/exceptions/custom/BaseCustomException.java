package com.compadres.na.exceptions.custom;

import org.springframework.http.HttpStatus;

/**
 * Clase base abstracta para todas las excepciones personalizadas del dominio.
 * Define el contrato unificado para todas las excepciones con error, description y status.
 */
public abstract class BaseCustomException extends RuntimeException {

    protected final String error;
    protected final String description;
    protected final HttpStatus status;

    protected BaseCustomException(String error, String description, HttpStatus status) {
        super(description);
        this.error = error;
        this.description = description;
        this.status = status;
    }

    protected BaseCustomException(String error, String description, HttpStatus status, Throwable cause) {
        super(description, cause);
        this.error = error;
        this.description = description;
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public String getDescription() {
        return description;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public int getStatusCode() {
        return status.value();
    }
}
