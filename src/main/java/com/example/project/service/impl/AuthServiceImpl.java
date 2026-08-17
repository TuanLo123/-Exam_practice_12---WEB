package com.example.project.service.impl;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.project.dto.request.ForgotPasswordRequest;
import com.example.project.dto.request.LoginRequest;
import com.example.project.dto.request.RegisterRequest;
import com.example.project.dto.request.ResetPasswordRequest;
import com.example.project.dto.response.LoginResponse;
import com.example.project.entity.Role;
import com.example.project.entity.User;
import com.example.project.repository.RoleRepository;
import com.example.project.repository.UserRepository;
import com.example.project.service.AuthService;
import com.example.project.service.EmailService;
import com.example.project.service.JwtService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
             AuthenticationManager authenticationManager, EmailService emailService, JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.jwtService = jwtService;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Tên đăng nhập hoặc mật khẩu không đúng"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Tên đăng nhập hoặc mật khẩu không đúng");
        }

        if ("locked".equalsIgnoreCase(user.getStatus())) {
            String reason = user.getLockReason() == null || user.getLockReason().isBlank()
                    ? "Không có lý do cụ thể" : user.getLockReason();
            throw new RuntimeException("Tài khoản đã bị khóa. Lý do: " + reason);
        }

        Authentication authentication = authenticationManager.authenticate(new
            UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(userDetails);
        String role = user.getRole().getRoleName();

        return new LoginResponse(token, role);
    }

    @Override
    public String register(RegisterRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        Optional<User> userOptional_username = userRepository.findByUsername(request.getUsername());

        if (userOptional.isPresent()) {
            throw new RuntimeException("Email đã tồn tại");
        }

        if (userOptional_username.isPresent()) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }

        Role userRole = roleRepository.findByRoleName("USER").orElseThrow(() -> new RuntimeException("Không tìm thấy ROLE USER"));

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setStatus("active");
        newUser.setWarningCount(0);
        newUser.setRole(userRole);
        userRepository.save(newUser);
        return "Đăng ký thành công";
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        emailService.requestForgotPassword(request.getEmail());
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }

    
}
