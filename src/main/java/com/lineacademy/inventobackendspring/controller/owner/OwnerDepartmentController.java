package com.lineacademy.inventobackendspring.controller.owner;

import com.lineacademy.inventobackendspring.domain.department.Department;
import com.lineacademy.inventobackendspring.dto.owner.department.request.AssignDepartmentManagerRequest;
import com.lineacademy.inventobackendspring.dto.owner.department.request.DepartmentRequest;
import com.lineacademy.inventobackendspring.dto.owner.department.response.OwnerDepartmentResponseDTO;
import com.lineacademy.inventobackendspring.service.owner.OwnerDepartmentService;
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
@RequestMapping("/owner/department")
@RequiredArgsConstructor
public class OwnerDepartmentController {

    private final OwnerDepartmentService ownerDepartmentService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDepartmentList(@AuthenticationPrincipal Long currentUserId) {
        try {
            List<Department> departments = ownerDepartmentService.getDepartmentList(currentUserId);

            List<OwnerDepartmentResponseDTO.DepartmentListResponse> responseData = departments.stream()
                    .map(OwnerDepartmentResponseDTO.DepartmentListResponse::from)
                    .toList();

            return ResponseEntity.ok(Map.of(
                    "message", "조직 부서 리스트를 불러왔습니다.",
                    "data", responseData
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("FORBIDDEN_APPROVAL")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "해당 정보를 조회할 권한이 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "조직 부서 리스트를 불러오는 중 오류가 발생했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createDepartment(
            @AuthenticationPrincipal Long currentUserId,
            @Valid @RequestBody DepartmentRequest request
    ) {
        try {
            Department department = ownerDepartmentService.createDepartment(currentUserId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "부서가 생성되었습니다.",
                    "data", OwnerDepartmentResponseDTO.DepartmentListResponse.from(department)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("FORBIDDEN_OWNER_ONLY") || e.getMessage().equals("FORBIDDEN_APPROVAL")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "대표자(OWNER)만 부서를 생성할 수 있습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "부서 생성 중 오류가 발생했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{departmentId}")
    public ResponseEntity<Map<String, Object>> updateDepartment(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long departmentId,
            @Valid @RequestBody DepartmentRequest request
    ) {
        try {
            Department updatedDepartment = ownerDepartmentService.updateDepartment(currentUserId, departmentId, request);
            return ResponseEntity.ok(Map.of(
                    "message", "부서 정보가 수정되었습니다.",
                    "data", OwnerDepartmentResponseDTO.DepartmentListResponse.from(updatedDepartment)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("FORBIDDEN_OWNER_ONLY") || e.getMessage().equals("FORBIDDEN_APPROVAL")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "대표자(OWNER)만 부서를 수정할 수 있습니다."));
            }
            if (e.getMessage().equals("DEPARTMENT_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 부서를 찾을 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "부서 수정 중 오류가 발생했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{departmentId}")
    public ResponseEntity<Map<String, Object>> deleteDepartment(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long departmentId
    ) {
        try {
            ownerDepartmentService.deleteDepartment(currentUserId, departmentId);
            return ResponseEntity.ok(Map.of("message", "부서 삭제를 완료했습니다."));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("FORBIDDEN_OWNER_ONLY") || e.getMessage().equals("FORBIDDEN_APPROVAL")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "대표자(OWNER)만 부서를 삭제할 수 있습니다."));
            }
            if (e.getMessage().equals("DEPARTMENT_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 부서를 찾을 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "부서 삭제 중 오류가 발생했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{departmentId}/manager")
    public ResponseEntity<Map<String, Object>> assignDepartmentManager(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long departmentId,
            @Valid @RequestBody AssignDepartmentManagerRequest request
    ) {
        try {
            ownerDepartmentService.assignDepartmentManager(currentUserId, departmentId, request);
            return ResponseEntity.ok(Map.of("message", "관리자 임명이 완료되었습니다."));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("FORBIDDEN_OWNER_ONLY") || e.getMessage().equals("FORBIDDEN_APPROVAL")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "대표자(OWNER)만 관리자를 임명할 수 있습니다."));
            }
            if (e.getMessage().equals("MEMBER_NOT_IN_DEPARTMENT")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "해당 부서에 소속된 회원만 관리자로 임명할 수 있습니다."));
            }
            if (e.getMessage().equals("DEPARTMENT_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 부서를 찾을 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "관리자 임명 중 오류가 발생했습니다."));
        }
    }
}