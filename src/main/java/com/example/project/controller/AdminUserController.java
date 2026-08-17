package com.example.project.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.project.dto.request.AdminUserRequest;
import com.example.project.dto.request.UpdateUserStatusRequest;
import com.example.project.dto.response.AdminUserResponse;
import com.example.project.security.UserPrincipal;
import com.example.project.service.AdminUserService;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {
    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public List<AdminUserResponse> getAllUsers() {
        return adminUserService.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody AdminUserRequest request) {
        try {
            return ResponseEntity.ok(adminUserService.createUser(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody AdminUserRequest request) {
        try {
            return ResponseEntity.ok(adminUserService.updateUser(id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Integer id,
            @RequestBody UpdateUserStatusRequest request, Authentication authentication) {
        try {
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            return ResponseEntity.ok(
                    adminUserService.updateStatus(id, request.getStatus(), request.getLockReason(), principal.getUserId()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
