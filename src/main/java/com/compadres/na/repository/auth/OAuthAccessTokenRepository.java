package com.compadres.na.repository.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import com.compadres.na.model.auth.OAuthAccessToken;

public interface OAuthAccessTokenRepository extends JpaRepository<OAuthAccessToken, Long> {
    // void save(OAuthAccessToken entitie);
}
