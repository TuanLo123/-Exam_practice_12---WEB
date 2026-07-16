package com.example.project.service;
import com.example.project.dto.request.LoginRequest;

public interface AuthService {
    String login(LoginRequest request);
}
