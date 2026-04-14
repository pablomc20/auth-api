package com.compadres.na.dto.auth;

import lombok.Builder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Builder
public record RegisterRequest(
        @NotBlank(message = "El nombre de usuario es requerido") @Size(min = 2, max = 100, message = "El nombre de usuario debe tener entre 2 y 100 caracteres") String name,

        @NotBlank(message = "La contraseña es requerida") @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres") String password,

        @NotBlank(message = "El email es requerido") @Email(message = "El email no tiene un formato valido") @Size(min = 5, max = 100, message = "El email debe tener entre 5 y 100 caracteres") String email,

        @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "El telefono debe contener entre 7 y 15 digitos y puede incluir '+' al inicio") String phone) {
}