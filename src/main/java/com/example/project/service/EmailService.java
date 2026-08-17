package com.example.project.service;

public interface EmailService {
    void sendOtpEmail(String email, String otpCode);
    void requestForgotPassword(String email);
    boolean verifyOtp(String email, String otpCode);
}
