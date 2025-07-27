package com.example.definex.taskmanagement.service;

import com.example.definex.taskmanagement.dto.request.LoginRequest;
import com.example.definex.taskmanagement.dto.request.RegisterRequest;
import com.example.definex.taskmanagement.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
}
