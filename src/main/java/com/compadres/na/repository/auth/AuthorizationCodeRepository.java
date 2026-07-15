package com.compadres.na.repository.auth;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.compadres.na.model.auth.AuthorizationCode;

public interface AuthorizationCodeRepository extends JpaRepository<AuthorizationCode, UUID> {

    Optional<AuthorizationCode> findByCodeAndClientIdAndUsedFalse(String code, String clientId);
}

