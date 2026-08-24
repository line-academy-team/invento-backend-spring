package com.lineacademy.inventobackendspring.controller;

import com.lineacademy.inventobackendspring.domain.user.User;
import com.lineacademy.inventobackendspring.dto.member.response.MemberInfoResponse;
import com.lineacademy.inventobackendspring.dto.user.request.*;
import com.lineacademy.inventobackendspring.dto.user.response.UserResponse;
import com.lineacademy.inventobackendspring.service.UserService;
import com.lineacademy.inventobackendspring.utils.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
            authData.put("memberInfo", MemberInfoResponse.from(user.getMember()));

            return ResponseEntity.ok(Map.of(
                    "message", "사용자 정보 확인이 완료되었습니다.",
                    "data", authData
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "유효하지 않은 사용자이거나 탈퇴한 계정입니다"));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> createUser(
            @Valid @RequestBody SignupRequest request
    ) {
        try {
            User newUser = userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "회원가입이 완료되었습니다.",
                    "data", UserResponse.from(newUser)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("ALREADY_EXISTS_EMAIL")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "이미 사용 중인 이메일입니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "유저 생성 중 오류가 발생했습니다."));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        try {
            User user = userService.login(request);
            String token = jwtUtil.generateToken(user.getId());

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("user", UserResponse.from(user));
            responseData.put("memberInfo", MemberInfoResponse.from(user.getMember()));
            responseData.put("token", token);

            return ResponseEntity.ok(Map.of(
                    "message", "로그인에 성공했습니다",
                    "data", responseData
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("INVALID_CREDENTIALS")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "이메일 또는 비밀번호가 일치하지 않습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "로그인 처리 중 서버 에러가 발생했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/update")
    public ResponseEntity<Map<String, Object>> updateUser(
            @AuthenticationPrincipal Long currentUserId,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        try {
            User updateUser = userService.updateUser(currentUserId, request);
            return ResponseEntity.ok(Map.of(
                    "message", "회원 정보가 성공적으로 수정되었습니다.",
                    "data", UserResponse.from(updateUser)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("NOT_FOUND_USER")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "해당 사용자를 찾을 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "서버 에러가 발생했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/password")
    public ResponseEntity<Map<String, Object>> updatePassword(
            @AuthenticationPrincipal Long currentUserId,
            @Valid @RequestBody UpdatePasswordRequest request
    ) {
        try {
            userService.updatePassword(currentUserId, request);
            return ResponseEntity.ok(Map.of("message", "비밀번호가 성공적으로 변경되었습니다."));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("NOT_FOUND_USER")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "해당 사용자를 찾을 수 없습니다."));
            }
            if (e.getMessage().equals("INVALID_PASSWORD")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "현재 비밀번호가 일치하지 않습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "비밀번호 수정 중 서버 에러가 발생했습니다."));
        }
    }


    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/withdraw")
    public ResponseEntity<Map<String, Object>> withdrawUser(
            @AuthenticationPrincipal Long currentUserId,
            @Valid @RequestBody WithdrawUserRequest request
    ) {
        try {
            userService.withdrawUser(currentUserId, request);
            return ResponseEntity.ok(Map.of("message", "회원 탈퇴가 성공적으로 처리되었습니다."));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("NOT_FOUND_USER")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "해당 사용자를 찾을 수 없습니다."));
            }
            if (e.getMessage().equals("INVALID_PASSWORD")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "현재 비밀번호가 일치하지 않습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "회원 탈퇴 중 서버 에러가 발생했습니다."));
        }
    }
}
