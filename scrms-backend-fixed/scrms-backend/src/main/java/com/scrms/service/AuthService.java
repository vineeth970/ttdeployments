package com.scrms.service;

import com.scrms.dto.request.LoginRequest;
import com.scrms.dto.request.RegisterRequest;
import com.scrms.dto.response.AuthResponse;
import com.scrms.dto.response.UserResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    UserResponse register(RegisterRequest request);
    UserResponse getCurrentUser(String email);
}
