package com.compadres.na.business;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.compadres.na.dto.auth.AuthResponse;
import com.compadres.na.dto.auth.LoginRequest;
import com.compadres.na.dto.auth.RegisterRequest;
import com.compadres.na.exceptions.custom.DataValidationException;
import com.compadres.na.model.auth.User;
import com.compadres.na.model.auth.UserDetail;
import com.compadres.na.model.user.PatchUserDetailsRq;
import com.compadres.na.model.user.PatchUserRq;
import com.compadres.na.repository.auth.AuthRepository;
import com.compadres.na.repository.user.UserRepository;
import com.compadres.na.service.config.JwtUtil;
import com.compadres.na.service.user.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthBusiness implements UserService {

    private final AuthenticationManager authenticationManager;

    private final AuthRepository authRepository;
    
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse authLogin(LoginRequest loginRequest) {

        String identifier = loginRequest.username();

        if (identifier == null) {
            throw new DataValidationException("Se requiere un email o teléfono para iniciar sesión.");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        identifier,
                        loginRequest.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();

        String token = jwtUtil.generateToken(user);

        return AuthResponse.builder()
                .email(user.getEmail())
                .name(user.getUserDetail().getLegal_representative())
                .role(user.getRole())
                .uriImage(user.getUserDetail().getUrlImage())
                .phone(user.getUserDetail().getPhone())
                .id(user.getUserId().toString())
                .token(token).build();
    }

    @Override
    public AuthResponse registerNewUser(RegisterRequest registerRequest) {

        boolean invalid = registerRequest.email() == null && registerRequest.phone() == null;

        if (invalid) {
            throw new DataValidationException("Se requiere un email o teléfono para el registro.");
        }

        User userExists = null;
        
        if (registerRequest.phone() != null) {
            userExists = authRepository
                .findByUserDetailPhone(registerRequest.phone())
                .orElse(null);

            if (userExists!= null && userExists.isEnabled()) {
                throw new DataValidationException("El teléfono ya está en uso por otro usuario.");
            }
        }

        if (userExists == null && registerRequest.email() != null) {
            userExists = authRepository
                .findByEmail(registerRequest.email())
                .orElse(null);

            if (userExists != null && userExists.isEnabled()) {
                throw new DataValidationException("El correo ya está en uso por otro usuario.");
            }
        }

        if (userExists != null) {
            return updateUserExisting(registerRequest, userExists);
        }


        // Si no existe un usuario con el email o teléfono proporcionado, se crea uno nuevo
        return createNewUser(registerRequest);
    }

    private AuthResponse createNewUser(RegisterRequest registerRequest) {
        User newUser = User.builder()
                .email(registerRequest.email())
                .password(passwordEncoder.encode(registerRequest.password()))
                .role("CLIENT") // 'CLIENT' por defecto
                .enabled(true) // Habilitado por defecto
                .build();

        UserDetail detail = UserDetail.builder()
                .legal_representative(registerRequest.name())
                .phone(registerRequest.phone())
                .build();

        detail.setUser(newUser);
        newUser.setUserDetail(detail);

        authRepository.save(newUser);
        
        String token = jwtUtil.generateToken(newUser);

        return AuthResponse.builder()
                .email(newUser.getEmail())
                .name(newUser.getUserDetail().getLegal_representative())
                .role(newUser.getRole())
                .uriImage(newUser.getUserDetail().getUrlImage())
                .id(newUser.getUserId().toString())
                .phone(newUser.getUserDetail().getPhone())
                .token(token)
                .build();
    }

    private AuthResponse updateUserExisting(RegisterRequest registerRequest, User user) {
        String idUserToModificate = user.getUserId().toString();

        PatchUserRq createUserRq = PatchUserRq.builder()
                .email(registerRequest.email())
                .password(passwordEncoder.encode(registerRequest.password()))
                .enabled(true)
                .build();

        try {
            userRepository.patchUser(createUserRq, idUserToModificate);
        } catch (DuplicateKeyException e) {
            throw new DataValidationException("El correo ya está en uso.");
        }

        PatchUserDetailsRq createUserDetailsRq = PatchUserDetailsRq.builder()
                .userId(idUserToModificate)
                .phone(registerRequest.phone())
                .legalRepresentative(registerRequest.name())
                .build();

        userRepository.patchUserDetails(createUserDetailsRq);

        String token = jwtUtil.generateToken(user);

        return AuthResponse.builder()
                .email(registerRequest.email())
                .name(registerRequest.name())
                .role(user.getRole())
                .uriImage(user.getUserDetail().getUrlImage())
                .id(idUserToModificate)
                .phone(registerRequest.phone())
                .token(token)
                .build();
    }

}
