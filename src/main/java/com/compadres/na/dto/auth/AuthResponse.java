package com.compadres.na.dto.auth;

import lombok.Builder;

@Builder
public record AuthResponse(
        String id,
        String name,
        String uriImage,
        String role,
        String email,
        String token) {
}