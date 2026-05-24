package com.compadres.na.dto.auth;

import lombok.Builder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Builder
public record LoginRequest(
                @Email(message = "El correo no tiene un formato valido") String email,
                @Pattern(regexp = "^\\d{10,13}$", message = "El teléfono no tiene un formato valido") String phone,
                @NotBlank(message = "La contraseña es requerida") String password) {
}
