package com.lineacademy.inventobackendspring.controller;

import com.lineacademy.inventobackendspring.domain.user.User;
import com.lineacademy.inventobackendspring.dto.user.response.UserResponse;
import com.lineacademy.inventobackendspring.service.UserService;
import com.lineacademy.inventobackendspring.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMe(
            @AuthenticationPrincipal Long currentUserId
    ) {
        try {
            User user = userService.getUserWithMemberInfo(currentUserId);

            Map<String, Object> authData = new HashMap<>();
            authData.put("user", UserResponse.from(user));

        } catch (RuntimeException e) {

        }
    }
}
