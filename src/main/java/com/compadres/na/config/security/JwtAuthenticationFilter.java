package com.compadres.na.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.compadres.na.service.config.CustomUserDetailsService;
import com.compadres.na.service.config.JwtUtil;
import com.compadres.na.exceptions.custom.AuthenticationException;
import com.compadres.na.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final CustomUserDetailsService userDetailsService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Verifica si el path de la solicitud requiere autenticación JWT.
     * Los paths públicos no requieren token.
     */
    private boolean requiresAuthentication(String requestURI) {
        return !(requestURI.startsWith("/auth/") ||
                requestURI.startsWith("/public/") ||
                requestURI.startsWith("/images/file/") ||
                requestURI.contains("/swagger-ui") ||
                requestURI.contains("/v3/api-docs") ||
                requestURI.equals("/swagger-ui.html"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            final String authHeader = request.getHeader("Authorization");
            final String jwt;
            final String username;

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                if (requiresAuthentication(request.getRequestURI())) {
                    throw new AuthenticationException("Token de autenticación requerido");
                }
                filterChain.doFilter(request, response);
                return;
            }

            jwt = authHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);
        } catch (AuthenticationException ex) {
            // Manejar la excepción directamente en el filtro
            ErrorResponse errorResponse = new ErrorResponse(
                    ex.getError(),
                    ex.getDescription(),
                    ex.getStatusCode());

            response.setStatus(ex.getStatus().value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }
}