package com.compadres.na.service.user;

import com.compadres.na.dto.auth.AuthResponse;
import com.compadres.na.dto.auth.LoginRequest;
import com.compadres.na.dto.auth.RegisterRequest;

public interface UserService {
  AuthResponse authLogin(LoginRequest loginRequest);

  void registerNewUser(RegisterRequest registerRequest);
}
