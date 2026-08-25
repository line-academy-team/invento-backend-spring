package com.lineacademy.inventobackendspring.controller.admin;

import com.lineacademy.inventobackendspring.dto.admin.user.request.AdminUpdateUserRequest;
import com.lineacademy.inventobackendspring.dto.admin.user.response.AdminUserResponseDTO;
import com.lineacademy.inventobackendspring.dto.user.request.LoginRequest;
import com.lineacademy.inventobackendspring.service.admin.AdminUserService;
import com.lineacademy.inventobackendspring.utils.JwtUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class AdminUserController {
    private final AdminUserService adminUserService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        try {
            User user = adminUserService.login(request);
            String token = jwtUtil.generateToken(user.getId());

            return ResponseEntity.ok(Map.of(
                    "message", "어드민 로그인에 성공했습니다.",
                    "data", Map.of(
                            "user", AdminUserResponseDTO.AdminUserResponse.from(user),
                            "token", token
                    )
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("INVALID_CREDENTIALS")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "이메일 또는 비밀번호가 일치하지 않습니다."));
            }
            if (e.getMessage().equals("NOT_ADMIN")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "어드민 권한이 없는 계정입니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러가 발생했습니다."));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUsers() {
        try {
            List<User> users = adminUserService.getUsers();

            List<AdminUserResponseDTO.AdminUserResponse> responseData = users.stream()
                    .map(AdminUserResponseDTO.AdminUserResponse::from)
                    .toList();

            return ResponseEntity.ok(Map.of(
                    "message", "사용자 목록을 성공적으로 조회했습니다.",
                    "data", responseData
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러가 발생했습니다."));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        try {
            User user = adminUserService.getUserById(id);
            return ResponseEntity.ok(Map.of(
                    "message", "사용자 상세 정보를 성공적으로 조회했습니다.",
                    "data", AdminUserResponseDTO.AdminUserDetailResponse.from(user)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("NOT_FOUND_USER")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 사용자를 찾을 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러가 발생했습니다."));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody AdminUpdateUserRequest request
    ) {
        try {
            User updatedUser = adminUserService.updateUser(id, request);
            return ResponseEntity.ok(Map.of(
                    "message", "사용자 정보가 성공적으로 수정되었습니다",
                    "data", AdminUserResponseDTO.AdminUserResponse.from(updatedUser)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("NOT_FOUND_USER")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 사용자를 찾을 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러가 발생했습니다."));
        }
    }
}
