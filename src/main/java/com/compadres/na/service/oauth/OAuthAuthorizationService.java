package com.compadres.na.service.oauth;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.compadres.na.dto.oauth.TokenResponse;
import com.compadres.na.exceptions.custom.OAuthException;
import com.compadres.na.model.auth.AuthorizationCode;
import com.compadres.na.model.auth.OAuthAccessToken;
import com.compadres.na.model.auth.User;
import com.compadres.na.repository.auth.AuthorizationCodeRepository;
import com.compadres.na.repository.auth.OAuthAccessTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthAuthorizationService {

    private static final String EXPECTED_RESPONSE_TYPE = "code";
    private static final String EXPECTED_GRANT_TYPE = "authorization_code";

    private final AuthenticationManager authenticationManager;
    private final AuthorizationCodeRepository authorizationCodeRepository;
    private final OAuthAccessTokenRepository tokenRepository;

    @Value("${alexa.oauth.client-id}")
    private String alexaClientId;

    @Value("${alexa.oauth.client-secret}")
    private String alexaClientSecret;

    @Value("${alexa.oauth.access-token-expiration-seconds:3600}")
    private long accessTokenExpirationSeconds;

    @Value("${alexa.oauth.code-ttl-seconds:300}")
    private long authorizationCodeTtlSeconds;

    public void validateAuthorizationRequest(String state, String clientId, String responseType, String redirectUri) {
        if (!StringUtils.hasText(state)) {
            throw OAuthException.invalidAuthorizationRequest("El parámetro state es obligatorio");
        }
        if (!StringUtils.hasText(clientId)) {
            throw OAuthException.invalidAuthorizationRequest("El parámetro client_id es obligatorio");
        }
        if (!EXPECTED_RESPONSE_TYPE.equals(responseType)) {
            throw OAuthException.invalidAuthorizationRequest("response_type debe ser code");
        }
        if (!StringUtils.hasText(redirectUri)) {
            throw OAuthException.invalidAuthorizationRequest("El parámetro redirect_uri es obligatorio");
        }
        validateClientId(clientId);
    }

    @Transactional
    public String createAuthorizationCode(String username, String password, String state, String clientId, String scope,
            String redirectUri) {
        validateAuthorizationRequest(state, clientId, EXPECTED_RESPONSE_TYPE, redirectUri);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        User user = (User) authentication.getPrincipal();
        String generatedCode = UUID.randomUUID().toString();

        AuthorizationCode authorizationCode = AuthorizationCode.builder()
                .code(generatedCode)
                .clientId(clientId)
                .redirectUri(redirectUri)
                .scope(scope)
                .state(state)
                .used(false)
                .expiresAt(OffsetDateTime.now().plusSeconds(authorizationCodeTtlSeconds))
                .user(user)
                .build();

        authorizationCodeRepository.save(Objects.requireNonNull(authorizationCode));
        return generatedCode;
    }

    @Transactional
    public TokenResponse exchangeCodeForTokens(String grantType, String code, String clientId, String clientSecret) {
        if (!EXPECTED_GRANT_TYPE.equals(grantType)) {
            throw OAuthException.unsupportedGrantType();
        }
        if (!StringUtils.hasText(code)) {
            throw OAuthException.invalidGrant("El authorization code es obligatorio");
        }

        validateClientCredentials(clientId, clientSecret);

        AuthorizationCode authorizationCode = authorizationCodeRepository
                .findByCodeAndClientIdAndUsedFalse(code, clientId)
                .orElseThrow(() -> OAuthException.invalidGrant("El authorization code es inválido"));

        if (authorizationCode.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw OAuthException.invalidGrant("El authorization code ha expirado");
        }

        authorizationCode.setUsed(true);
        authorizationCodeRepository.save(authorizationCode);

        String accessToken = UUID.randomUUID().toString();
        String refreshToken = UUID.randomUUID().toString();

        OAuthAccessToken tokenEntity = OAuthAccessToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(authorizationCode.getUser()) // Aquí recuperas al dueño
                .expiresAt(OffsetDateTime.now().plusSeconds(accessTokenExpirationSeconds))
                .build();

        tokenRepository.save(Objects.requireNonNull(tokenEntity));

        return TokenResponse.builder()
                .access_token(accessToken)
                .refresh_token(refreshToken)
                .token_type("Bearer")
                .expires_in(accessTokenExpirationSeconds)
                .build();
    }

    public Map<String, String> extractClientCredentials(String authorizationHeader, String clientId, String clientSecret) {
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Basic ")) {
            try {
                byte[] decoded = Base64.getDecoder().decode(authorizationHeader.substring(6));
                String[] credentials = new String(decoded, StandardCharsets.UTF_8).split(":", 2);
                if (credentials.length == 2) {
                    return Map.of(
                            "clientId", credentials[0],
                            "clientSecret", credentials[1]);
                }
            } catch (IllegalArgumentException ex) {
                throw OAuthException.invalidClient();
            }
        }

        return Map.of(
                "clientId", clientId,
                "clientSecret", clientSecret);
    }

    private void validateClientCredentials(String clientId, String clientSecret) {
        if (!StringUtils.hasText(clientId) || !StringUtils.hasText(clientSecret)) {
            throw OAuthException.invalidClient();
        }
        validateClientId(clientId);
        if (!alexaClientSecret.equals(clientSecret)) {
            throw OAuthException.invalidClient();
        }
    }

    private void validateClientId(String clientId) {
        if (!alexaClientId.equals(clientId)) {
            throw OAuthException.invalidClient();
        }
    }
}
