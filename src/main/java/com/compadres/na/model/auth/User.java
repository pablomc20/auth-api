package com.compadres.na.model.auth;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users") // Nombre exacto de tu tabla
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // PostgreSQL manejará el UUID
    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(columnDefinition = "boolean default true")
    private boolean enabled;

    private String role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserDetail userDetail;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // --- Métodos de UserDetails ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Mapeamos el 'role' (CHAR) a una autoridad de Spring Security
        // Asumimos 'A' -> ROLE_ADMIN, 'U' -> ROLE_USER
        String roleName = "ROLE_USER"; // Default
        if (this.role != null) {
            if (this.role == "EMPLOYEE") {
                roleName = "ROLE_ADMIN";
            }
            // Agrega más mapeos si es necesario
        }
        return List.of(new SimpleGrantedAuthority(roleName));
    }

    @Override
    public String getUsername() {
        // Usamos el email como "username" para la autenticación
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Puedes agregar lógica si tienes expiración de cuentas
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Puedes agregar lógica para bloquear cuentas
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Puedes agregar lógica para expiración de credenciales
    }

    @Override
    public boolean isEnabled() {
        // Usamos el campo 'enabled' de la base de datos
        return this.enabled;
    }
}