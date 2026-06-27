package com.compadres.na.dto.auth;

import lombok.Builder;
import jakarta.validation.constraints.NotBlank;

@Builder
public record LoginRequest(
        String username,
        @NotBlank(message = "La contraseña es requerida") String password) {
}
