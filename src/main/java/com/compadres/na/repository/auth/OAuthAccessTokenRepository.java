package com.compadres.na.repository.auth;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.compadres.na.model.auth.OAuthAccessToken;

@Repository
public interface OAuthAccessTokenRepository extends JpaRepository<OAuthAccessToken, UUID> {
    // void save(OAuthAccessToken entitie);
}
