package com.lineacademy.inventobackendspring.controller.admin;

import com.lineacademy.inventobackendspring.domain.organization.Organization;
import com.lineacademy.inventobackendspring.dto.admin.organization.request.AdminUpdateOrganizationRequest;
import com.lineacademy.inventobackendspring.dto.admin.organization.response.AdminOrganizationResponseDTO;
import com.lineacademy.inventobackendspring.service.admin.AdminOrganizationService;
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
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrganizationController {

    private final AdminOrganizationService adminOrganizationService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getOrganizationList() {
        try {
            List<Organization> orgs = adminOrganizationService.getOrganizationList();
            List<AdminOrganizationResponseDTO.OrganizationDetail> responseList = orgs.stream()
                    .map(AdminOrganizationResponseDTO.OrganizationDetail::from)
                    .toList();

            return ResponseEntity.ok(Map.of(
                    "message", "조직 목록을 조회했습니다.",
                    "data", responseList
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "조직 목록 조회 중 오류가 발생했습니다."
            ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getOrganizationById(@PathVariable Long id) {
        try {
            Organization org = adminOrganizationService.getOrganizationById(id);
            return ResponseEntity.ok(Map.of(
                    "message", "조직 상세 정보를 조회했습니다.",
                    "data", AdminOrganizationResponseDTO.OrganizationDetail.from(org)
            ));
        } catch (RuntimeException e) {
            if ("ORGANIZATION_NOT_FOUND".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "message", "조직을 찾을 수 없습니다."
                ));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "조직 상세 정보를 불러오는 중 오류가 발생했습니다."
            ));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateOrganization(
            @PathVariable Long id,
            @Valid @RequestBody AdminUpdateOrganizationRequest request
    ) {
        try {
            Organization updatedOrg = adminOrganizationService.updateOrganization(id, request);
            return ResponseEntity.ok(Map.of(
                    "message", "단체 정보가 성공적으로 수정되었습니다.",
                    "data", AdminOrganizationResponseDTO.OrganizationDetail.from(updatedOrg)
            ));
        } catch (RuntimeException e) {
            if ("NOT_FOUND_ORGANIZATION".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "message", "해당 단체를 찾을 수 없습니다."
                ));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "서버 에러가 발생했습니다."
            ));
        }
    }
}
