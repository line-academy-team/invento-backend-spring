package com.lineacademy.inventobackendspring.service;

import com.lineacademy.inventobackendspring.domain.user.User;
import com.lineacademy.inventobackendspring.dto.user.request.*;
import com.lineacademy.inventobackendspring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public User getUserWithMemberInfo(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND_USER"));

        if (user.getDeletedAt() != null) {
            throw new RuntimeException("NOT_FOUND_USER");
        }

        return user;
    }

    @Transactional
    public User createUser(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("ALREADY_EXISTS_EMAIL");
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .build();

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("INVALID_CREDENTIALS"));

        if (user.getDeletedAt() != null) {
            throw new RuntimeException("INVALID_CREDENTIALS");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("INVALID_CREDENTIALS");
        }

        return user;
    }

    @Transactional
    public User updateUser(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND_USER"));

        if (user.getDeletedAt() != null) {
            throw new RuntimeException("NOT_FOUND_USER");
        }

        if (request.getName() != null) {
            user.updateName(request.getName());
        }

        if (request.getImageUrl() != null) {
            user.updateImageUrl(request.getImageUrl());
        }

        return user;
    }


    @Transactional
    public void updatePassword(Long userId, UpdatePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND_USER"));

        if (!passwordEncoder.matches(request.getCurrentPassword(),
                user.getPasswordHash())) {
            throw new RuntimeException("INVALID_PASSWORD");
        }

        user.updatePasswordHash(passwordEncoder.encode(request.getNewPassword()));
    }

    @Transactional
    public void withdrawUser(Long userId, WithdrawUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND_USER"));

        if (user.getDeletedAt() != null) {
            throw new RuntimeException("NOT_FOUND_USER");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("INVALID_PASSWORD");
        }

        user.markAsDeleted();
    }
}
