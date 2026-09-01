package com.lineacademy.inventobackendspring.controller.manager;

import com.lineacademy.inventobackendspring.dto.manager.dashboard.response.ManagerDashboardResponseDto.DashboardResponse;
import com.lineacademy.inventobackendspring.service.manager.ManagerDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/manager/dashboard")
@RequiredArgsConstructor
public class ManagerDashboardController {

    private final ManagerDashboardService managerDashboardService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboard(@AuthenticationPrincipal Long currentUserId) {
        try {
            DashboardResponse dashboardData = managerDashboardService.getDashboardData(currentUserId);

            return ResponseEntity.ok(Map.of(
                    "message", "매니저 대시보드 데이터를 성공적으로 조회했습니다.",
                    "data", dashboardData
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("MANAGER_PERMISSION_REQUIRED")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "대시보드 조회 권한이 없습니다."));
            }
            if (e.getMessage().equals("NOT_FOUND_ORGANIZATION")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 조직을 찾을 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "서버 에러가 발생했습니다."));
        }
    }
}