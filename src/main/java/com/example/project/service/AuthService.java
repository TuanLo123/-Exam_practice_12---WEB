package com.example.project.service;
import com.example.project.dto.request.ForgotPasswordRequest;
import com.example.project.dto.request.LoginRequest;
import com.example.project.dto.request.RegisterRequest;
import com.example.project.entity.Student;

public interface AuthService {
    String login(LoginRequest request);
    String register(RegisterRequest request);
    String forgotPassword(ForgotPasswordRequest request);
    void requestForgotPassword(String email);

}
