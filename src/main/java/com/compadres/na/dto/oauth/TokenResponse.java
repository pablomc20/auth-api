package com.compadres.na.dto.oauth;

import lombok.Builder;

@Builder
public record TokenResponse(
        String access_token,
        String refresh_token,
        String token_type,
        long expires_in) {
}
