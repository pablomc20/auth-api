package com.compadres.na.dto.auth;

import lombok.Builder;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Builder
public record RegisterRequest(
        @NotEmpty(message = "El nombre de usuario es requerido") @Size(min = 2, max = 100, message = "El nombre de usuario debe tener entre 2 y 100 caracteres") String name,

        @NotEmpty(message = "La contraseña es requerida") String password,

        @NotEmpty(message = "El email es requerido") @Size(min = 5, max = 100, message = "El email debe tener entre 5 y 100 caracteres") String email,

        String phone) {
}