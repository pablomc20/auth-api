package com.compadres.na.dto.auth;

import lombok.Builder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Builder
public record LoginRequest(
        @NotBlank(message = "El correo es requerido") @Email(message = "El correo no tiene un formato valido") String email,
        @NotBlank(message = "La contraseña es requerida") String password) {
}
