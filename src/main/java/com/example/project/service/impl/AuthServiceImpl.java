package com.example.project.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.project.dto.request.ForgotPasswordRequest;
import com.example.project.dto.request.LoginRequest;
import com.example.project.dto.request.RegisterRequest;
import com.example.project.entity.otp;
import com.example.project.entity.user;
import com.example.project.repository.OtpRepository;
import com.example.project.repository.UserRepository;
import com.example.project.service.AuthService;
import com.example.project.service.EmailService; 

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpRepository otpRepository;

    @Override
    public String login(LoginRequest request) {
        Optional<user> userOptional = userRepository.findByUsername(request.getUsername());

        if (userOptional.isEmpty()) {
            throw new RuntimeException("Không tìm thấy tài khoản");
        }

        user user = userOptional.get();

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Mật khẩu hoặc tài khoản bị sai!");
        }

        if (user.getStatus().equals("disabled")) {
            throw new RuntimeException("Tài khoản đã bị khóa");
        }

        return "Đăng nhập thành công";
    }

    @Override
    public String register(RegisterRequest request) {
        Optional<user> userOptional = userRepository.findByEmail(request.getEmail());
        Optional<user> userOptional_username = userRepository.findByUsername(request.getUsername());

        if (userOptional.isPresent()) {
            throw new RuntimeException("Email đã tồn tại");
        }

        if (userOptional_username.isPresent()) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }

        user newUser = new user();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(request.getPassword());
        newUser.setStatus("active");
        newUser.setWarningCount(0);
        newUser.setRoleId(1);
        userRepository.save(newUser);
        return "Đăng ký thành công";
    }

    @Override
    public String forgotPassword(ForgotPasswordRequest request) {
        Optional<user> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            throw new RuntimeException("Không tìm thấy tài khoản");
        }

        user user = userOptional.get();

        int OTP = (int) (Math.random() * 900000) + 100000;
        
        return "Mã OTP đã được gửi đến email của bạn: " + OTP;
    }

    @Override
    public void requestForgotPassword(String email) {
        user user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        String otpCode = generateOtp();

        LocalDateTime expiredTime = LocalDateTime.now().plusMinutes(5);

        otp otp = otpRepository.findByEmail(email).orElse(new otp());
        otp.setEmail(email);
        otp.setOtpCode(otpCode);
        otp.setExpiredTime(expiredTime);

        otpRepository.save(otp);

        emailService.sendOtpEmail(email, otpCode);
    }

    private String generateOtp() {
        Random random = new Random();
        int number = random.nextInt(900000) + 100000;
        return String.valueOf(number);
    }
}