package com.example.project.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.project.dto.request.AdminUserRequest;
import com.example.project.dto.response.AdminUserResponse;
import com.example.project.entity.Role;
import com.example.project.entity.User;
import com.example.project.repository.RoleRepository;
import com.example.project.repository.UserRepository;
import com.example.project.service.AdminUserService;

@Service
public class AdminUserServiceImpl implements AdminUserService {
    private static final Set<String> ALLOWED_ROLES = Set.of("USER", "ADMIN");
    private static final Set<String> ALLOWED_STATUSES = Set.of("active", "locked");

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<AdminUserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public AdminUserResponse createUser(AdminUserRequest request) {
        validateRequest(request, true);

        if ("locked".equals(normalizeStatus(request.getStatus()))) {
            throw new RuntimeException("Hãy tạo tài khoản hoạt động rồi dùng chức năng khóa để nhập lý do");
        }

        if (userRepository.findByUsername(request.getUsername().trim()).isPresent()) {
            throw new RuntimeException("Username đã tồn tại");
        }

        if (userRepository.findByEmail(request.getEmail().trim()).isPresent()) {
            throw new RuntimeException("Email đã tồn tại");
        }

        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setEmail(request.getEmail().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(findRole(request.getRole()));
        user.setStatus(normalizeStatus(request.getStatus()));
        user.setWarningCount(0);

        return toResponse(userRepository.save(user));
    }

    @Override
    public AdminUserResponse updateUser(Integer id, AdminUserRequest request) {
        validateRequest(request, false);
        User user = findUser(id);

        userRepository.findByUsername(request.getUsername().trim())
                .filter(existing -> existing.getId() != id)
                .ifPresent(existing -> { throw new RuntimeException("Username đã tồn tại"); });

        userRepository.findByEmail(request.getEmail().trim())
                .filter(existing -> existing.getId() != id)
                .ifPresent(existing -> { throw new RuntimeException("Email đã tồn tại"); });

        user.setUsername(request.getUsername().trim());
        user.setEmail(request.getEmail().trim());
        user.setRole(findRole(request.getRole()));
        String newStatus = normalizeStatus(request.getStatus());
        if ("locked".equals(newStatus) && !"locked".equals(user.getStatus())) {
            throw new RuntimeException("Hãy dùng nút khóa tài khoản để nhập lý do");
        }
        user.setStatus(newStatus);
        if ("active".equals(newStatus)) user.setLockReason(null);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return toResponse(userRepository.save(user));
    }

    @Override
    public AdminUserResponse updateStatus(Integer id, String status, String lockReason, Integer currentUserId) {
        if (id.equals(currentUserId) && "locked".equals(status)) {
            throw new RuntimeException("Không thể tự khóa tài khoản đang đăng nhập");
        }

        if ("locked".equals(status) && (lockReason == null || lockReason.isBlank())) {
            throw new RuntimeException("Vui lòng nhập lý do khóa tài khoản");
        }

        User user = findUser(id);
        user.setStatus(normalizeStatus(status));
        user.setLockReason("locked".equals(status) ? lockReason.trim() : null);
        return toResponse(userRepository.save(user));
    }

    private void validateRequest(AdminUserRequest request, boolean passwordRequired) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new RuntimeException("Username không được để trống");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new RuntimeException("Email không được để trống");
        }
        if (passwordRequired && (request.getPassword() == null || request.getPassword().length() < 6)) {
            throw new RuntimeException("Mật khẩu phải có ít nhất 6 ký tự");
        }
        if (!ALLOWED_ROLES.contains(request.getRole())) {
            throw new RuntimeException("Vai trò không hợp lệ");
        }
        normalizeStatus(request.getStatus());
    }

    private Role findRole(String roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò " + roleName));
    }

    private User findUser(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
    }

    private String normalizeStatus(String status) {
        String value = status == null ? "active" : status.trim().toLowerCase();
        if (!ALLOWED_STATUSES.contains(value)) {
            throw new RuntimeException("Trạng thái không hợp lệ");
        }
        return value;
    }

    private AdminUserResponse toResponse(User user) {
        return new AdminUserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().getRoleName(),
                user.getStatus(),
                user.getWarningCount(),
                user.getLockReason());
    }
}
