package com.example.project.service;

import java.util.List;

import com.example.project.dto.request.AdminUserRequest;
import com.example.project.dto.response.AdminUserResponse;

public interface AdminUserService {
    List<AdminUserResponse> getAllUsers();
    AdminUserResponse createUser(AdminUserRequest request);
    AdminUserResponse updateUser(Integer id, AdminUserRequest request);
    AdminUserResponse updateStatus(Integer id, String status, String lockReason, Integer currentUserId);
}
