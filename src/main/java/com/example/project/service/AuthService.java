package com.example.project.service;
import com.example.project.dto.request.ForgotPasswordRequest;
import com.example.project.dto.request.LoginRequest;
import com.example.project.dto.request.RegisterRequest;
import com.example.project.dto.request.ResetPasswordRequest;
import com.example.project.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    String register(RegisterRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
}
