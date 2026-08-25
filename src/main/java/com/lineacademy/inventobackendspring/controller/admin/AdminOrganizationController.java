package com.lineacademy.inventobackendspring.controller.admin;

import com.lineacademy.inventobackendspring.domain.organization.Organization;
import com.lineacademy.inventobackendspring.dto.admin.organization.request.AdminUpdateOrganizationRequest;
import com.lineacademy.inventobackendspring.dto.admin.organization.response.AdminOrganizationResponseDTO;
import com.lineacademy.inventobackendspring.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/admin/organization")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminOrganizationController {

    private final OrganizationService organizationService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getOrganizationList() {
        try {
            List<Organization> organizations = organizationService.getOrganizationList();

            List<AdminOrganizationResponseDTO.AdminOrganizationResponse> responseData = organizations.stream()
                    .map(AdminOrganizationResponseDTO.AdminOrganizationResponse::from)
                    .toList();

            return ResponseEntity.ok(Map.of(
                    "message", "조직 목록을 조회했습니다.",
                    "data", responseData
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "조직 목록 조회 중 오류가 발생했습니다."));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getOrganizationById(@PathVariable Long id) {
        try {
            Organization organization = organizationService.getOrganizationById(id);
            return ResponseEntity.ok(Map.of(
                    "message", "조직 상세 정보를 조회했습니다.",
                    "data", AdminOrganizationResponseDTO.AdminOrganizationResponse.from(organization)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("ORGANIZATION_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "조직을 찾을 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "조직 상세 정보를 불러오는 중 오류가 발생했습니다."));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateOrganization(
            @PathVariable Long id,
            @Valid @RequestBody AdminUpdateOrganizationRequest request
    ) {
        try {
            Organization updatedOrg = organizationService.updateOrganization(id, request);
            return ResponseEntity.ok(Map.of(
                    "message", "단체 정보가 성공적으로 수정되었습니다.",
                    "data", AdminOrganizationResponseDTO.AdminOrganizationResponse.from(updatedOrg)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("NOT_FOUND_ORGANIZATION")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 단체를 찾을 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "서버 에러가 발생했습니다."));
        }
    }
}