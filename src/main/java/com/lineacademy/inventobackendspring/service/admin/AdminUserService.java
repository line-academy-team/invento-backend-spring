package com.lineacademy.inventobackendspring.service.admin;

import com.lineacademy.inventobackendspring.domain.enums.UserRole;
import com.lineacademy.inventobackendspring.domain.user.User;
import com.lineacademy.inventobackendspring.dto.admin.user.request.AdminLoginRequest;
import com.lineacademy.inventobackendspring.dto.admin.user.request.AdminUpdateUserRequest;
import com.lineacademy.inventobackendspring.repository.UserRepository;
import com.lineacademy.inventobackendspring.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public Map<String, Object> login(AdminLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("INVALID_CREDENTIALS"));

        if (user.getDeletedAt() != null) {
            throw new RuntimeException("INVALID_CREDENTIALS");
        }

        if (user.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("NOT_ADMIN");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("INVALID_CREDENTIALS");
        }

        String token = jwtUtil.generateToken(user.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("token", token);
        return result;
    }

    @Transactional(readOnly = true)
    public List<User> getUsers() {
        return userRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND_USER"));
    }

    @Transactional
    public User updateUser(Long userId, AdminUpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND_USER"));

        if (request.getRole() != null) {
            user.updateRole(request.getRole());
        }
        if (Boolean.TRUE.equals(request.getIsDeleted())) {
            user.markAsDeleted();
        } else if (Boolean.FALSE.equals(request.getIsDeleted())) {
            user.restore();
        }

        return user;
    }
}
