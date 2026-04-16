package com.compadres.na.controller;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.compadres.na.dto.oauth.TokenResponse;
import com.compadres.na.service.oauth.OAuthAuthorizationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
public class TokenController {

    private final OAuthAuthorizationService oAuthAuthorizationService;

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponse> exchangeToken(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestParam("grant_type") String grantType,
            @RequestParam String code,
            @RequestParam(value = "client_id", required = false) String clientId,
            @RequestParam(value = "client_secret", required = false) String clientSecret) {
        Map<String, String> clientCredentials = oAuthAuthorizationService.extractClientCredentials(
                authorizationHeader,
                clientId,
                clientSecret);

        TokenResponse response = oAuthAuthorizationService.exchangeCodeForTokens(
                grantType,
                code,
                clientCredentials.get("clientId"),
                clientCredentials.get("clientSecret"));

        return ResponseEntity.ok(response);
    }
}
