package com.lineacademy.inventobackendspring.controller.manager;

import com.lineacademy.inventobackendspring.domain.department.Department;
import com.lineacademy.inventobackendspring.domain.member.Member;
import com.lineacademy.inventobackendspring.dto.manager.department.request.TransferDepartmentRequest;
import com.lineacademy.inventobackendspring.dto.manager.department.response.ManagerDepartmentResponseDTO;
import com.lineacademy.inventobackendspring.service.manager.ManagerDepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/manager/department")
@RequiredArgsConstructor
public class ManagerDepartmentController {

    private final ManagerDepartmentService departmentService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getOrgMemberList(
            @AuthenticationPrincipal Long currentUserId,
            @RequestParam(required = false) String search
    ) {
        try {
            List<Member> members = departmentService.getOrgMemberList(currentUserId, search);

            List<ManagerDepartmentResponseDTO.OrgMemberResponse> responseData = members.stream()
                    .map(ManagerDepartmentResponseDTO.OrgMemberResponse::from)
                    .toList();

            return ResponseEntity.ok(Map.of(
                    "message", "조직 멤버 리스트를 불러왔습니다.",
                    "data", responseData
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("FORBIDDEN_APPROVAL")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "해당 정보를 조회할 권한이 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "조직 멤버 리스트를 불러오는 중 오류가 발생했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{departmentId}")
    public ResponseEntity<Map<String, Object>> getDepartmentById(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long departmentId
    ) {
        try {
            Department department = departmentService.getDepartmentById(currentUserId, departmentId);
            return ResponseEntity.ok(Map.of(
                    "message", "부서 상세 정보를 불러왔습니다.",
                    "data", ManagerDepartmentResponseDTO.DepartmentDetailResponse.from(department)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("FORBIDDEN_APPROVAL") || e.getMessage().equals("FORBIDDEN_DEPARTMENT_VIEW")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "해당 정보를 조회할 권한이 없습니다."));
            }
            if (e.getMessage().equals("DEPARTMENT_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 부서를 찾을 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "부서 정보를 불러오는 중 오류가 발생했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/transfer")
    public ResponseEntity<Map<String, Object>> transferDepartment(
            @AuthenticationPrincipal Long currentUserId,
            @Valid @RequestBody TransferDepartmentRequest request
    ) {
        try {
            departmentService.transferDepartment(currentUserId, request);
            return ResponseEntity.ok(Map.of("message", "성공적으로 부서 이동이 완료되었습니다."));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("FORBIDDEN_APPROVAL")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "부서 이동을 처리할 권한이 없습니다."));
            }
            if (e.getMessage().equals("DEPARTMENT_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "이동하려는 부서를 찾을 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "부서 이동 처리 중 오류가 발생했습니다."));
        }
    }
}