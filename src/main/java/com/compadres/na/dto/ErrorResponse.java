package com.compadres.na.dto;

import lombok.Builder;

/**
 * DTO para respuestas de error estandarizadas.
 * Todas las excepciones personalizadas devolverán este formato.
 */
@Builder
public record ErrorResponse(
        String error,
        String description,
        int status) {

}