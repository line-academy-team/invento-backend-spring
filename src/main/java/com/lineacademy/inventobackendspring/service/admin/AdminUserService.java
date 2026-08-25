package com.lineacademy.inventobackendspring.service.admin;

import com.lineacademy.inventobackendspring.domain.enums.UserRole;
import com.lineacademy.inventobackendspring.domain.user.User;
import com.lineacademy.inventobackendspring.dto.admin.user.request.AdminUpdateUserRequest;
import com.lineacademy.inventobackendspring.dto.user.request.LoginRequest;
import com.lineacademy.inventobackendspring.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AdminUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public User login(LoginRequest request) {
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

        return user;
    }

    @Transactional(readOnly = true)
    public List<User> getUsers() {
        return userRepository.findAllUsersIncludingDeleted();
    }

    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findByIdIncludingDeleted(userId)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND_USER"));
    }

    @Transactional
    public User updateUser(Long userId, AdminUpdateUserRequest request) {
        User user = userRepository.findByIdIncludingDeleted(userId)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND_USER"));

        if (request.getName() != null) {
            user.updateName(request.getName());
        }
        if (request.getRole() != null) {
            user.updateRole(request.getRole());
        }

        if (request.getIsDeleted() != null) {
            if (request.getIsDeleted()) {
                user.markAsDeleted();
            } else {
                user.restore();
            }
        }

        return user;
    }
}
