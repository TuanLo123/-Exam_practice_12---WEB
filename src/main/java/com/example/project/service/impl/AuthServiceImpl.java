package com.example.project.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.project.dto.request.LoginRequest;
import com.example.project.entity.user;
import com.example.project.repository.UserRepository;
import com.example.project.service.AuthService; 

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserRepository userRepository;

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
}