package com.example.project.dto.response;

public record AdminUserResponse(
        Integer id,
        String username,
        String email,
        String role,
        String status,
        int warningCount,
        String lockReason) {
}
