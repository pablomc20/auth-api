package com.compadres.na.service.config;

import com.compadres.na.exceptions.custom.AuthenticationException;
import com.compadres.na.model.auth.User;
import com.compadres.na.repository.auth.UserRepositoryImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepositoryImpl userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailWithDetail(username)
                .orElseThrow(() -> AuthenticationException.userNotFound(username));

        return user;
    }
}