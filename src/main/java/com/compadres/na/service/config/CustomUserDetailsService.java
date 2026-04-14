package com.compadres.na.service.config;

import com.compadres.na.exceptions.custom.AuthenticationException;
import com.compadres.na.model.auth.User;
import com.compadres.na.repository.auth.UserRepositoryImpl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepositoryImpl userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailWithDetail(username)
                .orElseThrow(() -> AuthenticationException.userNotFound(username));

        return user;
    }
}