package com.compadres.na.repository.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.compadres.na.model.auth.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUserDetailPhone(String phone);

    @Query("""
        SELECT u FROM User u
        LEFT JOIN FETCH u.userDetail ud
        WHERE u.email = :identifier
        OR ud.phone = :identifier
    """)
    Optional<User> findByEmailOrPhone(
        @Param("identifier") String identifier
    );

}