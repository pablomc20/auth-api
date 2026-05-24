package com.compadres.na.exceptions.custom;

import org.springframework.http.HttpStatus;

/**
 * Excepción personalizada para errores de validación de datos de entrada.
 * Se utiliza cuando los datos proporcionados por el cliente no cumplen con las reglas de validación.
 */
public class DataValidationException extends BaseCustomException {

    public DataValidationException(String description) {
        super("DATA_VALIDATION_ERROR", description, HttpStatus.BAD_REQUEST);
    }

    public DataValidationException(String description, Throwable cause) {
        super("DATA_VALIDATION_ERROR", description, HttpStatus.BAD_REQUEST, cause);
    }

    public static DataValidationException fieldRequired(String fieldName) {
        return new DataValidationException("El campo '" + fieldName + "' es requerido");
    }

    public static DataValidationException invalidValue(String fieldName, String value) {
        return new DataValidationException("El valor '" + value + "' no es válido para el campo '" + fieldName + "'");
    }

    public static DataValidationException invalidFormat(String fieldName, String expectedFormat) {
        return new DataValidationException("El formato del campo '" + fieldName + "' no es válido. Se esperaba: " + expectedFormat);
    }
}