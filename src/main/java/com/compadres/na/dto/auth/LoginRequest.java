package com.compadres.na.dto.auth;

import lombok.Builder;
import jakarta.validation.constraints.NotEmpty;

@Builder
public record LoginRequest(
        @NotEmpty(message = "El correo es requerido") String email,
        @NotEmpty(message = "La contraseña es requerida") String password) {
}
