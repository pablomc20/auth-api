package com.compadres.na.controller;


import java.net.URI;
import java.util.Objects;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.compadres.na.dto.auth.AuthResponse;
import com.compadres.na.dto.auth.LoginRequest;
import com.compadres.na.dto.auth.RegisterRequest;
import com.compadres.na.service.oauth.OAuthAuthorizationService;
import com.compadres.na.service.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final OAuthAuthorizationService oAuthAuthorizationService;

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> authorizationPage(
            @RequestParam String state,
            @RequestParam("client_id") String clientId,
            @RequestParam("response_type") String responseType,
            @RequestParam(required = false) String scope,
            @RequestParam("redirect_uri") String redirectUri) {
        oAuthAuthorizationService.validateAuthorizationRequest(state, clientId, responseType, redirectUri);

        String html = """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <title>Alexa Account Linking</title>
                </head>
                <body>
                    <h1>Vincular cuenta</h1>
                    <p>Inicia sesión para vincular tu cuenta con Alexa.</p>
                    <form method="post" action="/auth/login">
                        <label for="username">Username o email</label><br/>
                        <input id="username" name="username" type="text" required /><br/><br/>
                        <label for="password">Password</label><br/>
                        <input id="password" name="password" type="password" required /><br/><br/>
                        <input type="hidden" name="state" value="%s" />
                        <input type="hidden" name="client_id" value="%s" />
                        <input type="hidden" name="response_type" value="%s" />
                        <input type="hidden" name="scope" value="%s" />
                        <input type="hidden" name="redirect_uri" value="%s" />
                        <button type="submit">Autorizar</button>
                    </form>
                </body>
                </html>
                """.formatted(
                escapeHtml(state),
                escapeHtml(clientId),
                escapeHtml(responseType),
                escapeHtml(scope),
                escapeHtml(redirectUri));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE)
                .body(html);
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = userService.authLogin(loginRequest);

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> oauthLogin(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String state,
            @RequestParam("client_id") String clientId,
            @RequestParam("response_type") String responseType,
            @RequestParam(required = false) String scope,
            @RequestParam("redirect_uri") String redirectUri) {
        oAuthAuthorizationService.validateAuthorizationRequest(state, clientId, responseType, redirectUri);

        String code = oAuthAuthorizationService.createAuthorizationCode(username, password, state, clientId, scope,
                redirectUri);

        URI location = UriComponentsBuilder.fromUri(Objects.requireNonNull(URI.create(redirectUri)))
                .queryParam("code", code)
                .queryParam("state", state)
                .build(true)
                .toUri();

        return ResponseEntity.status(302)
                .header(HttpHeaders.LOCATION, location.toString())
                .build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        userService.registerNewUser(request);
        return ResponseEntity.ok("Usuario registrado exitosamente");
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

}