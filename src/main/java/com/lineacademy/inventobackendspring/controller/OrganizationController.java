package com.lineacademy.inventobackendspring.controller;

import com.lineacademy.inventobackendspring.domain.member.Member;
import com.lineacademy.inventobackendspring.domain.organization.Organization;
import com.lineacademy.inventobackendspring.dto.organization.request.CreateOrganizationRequest;
import com.lineacademy.inventobackendspring.dto.organization.request.JoinOrganizationRequest;
import com.lineacademy.inventobackendspring.dto.organization.request.UpdateOrganizationRequest;
import com.lineacademy.inventobackendspring.dto.organization.response.OrganizationResponseDTO;
import com.lineacademy.inventobackendspring.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/organization")
@RequiredArgsConstructor
public class OrganizationController {
    private final OrganizationService organizationService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getOrganizationById(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long id
    ) {
        try {
            Organization org = organizationService.getOrganizationById(id, currentUserId);
            return ResponseEntity.ok(Map.of(
                    "message", "조직 정보를 성공적으로 불러왔습니다.",
                    "data", OrganizationResponseDTO.OrganizationDetailResponse.from(org)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("ORGANIZATION_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "message", "조직을 찾을 수 없습니다."
                ));
            }
            if (e.getMessage().equals("NOT_A_MEMBER_OF_ORGANIZATION")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "message", "해당 단체의 멤버만 조회할 수 있습니다."
                ));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "조직 정보 불러오기 중 서버 에러가 발생했습니다."
            ));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createOrganization(
            @AuthenticationPrincipal Long currentUserId,
            @Valid @RequestBody
            CreateOrganizationRequest request
    ) {
        try {
            Organization newOrg = organizationService.createOrganization(currentUserId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "조직이 성공적으로 생성되었으며 대표자로 등록되었습니다.",
                    "data", OrganizationResponseDTO.OrganizationResponse.from(newOrg)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("ALREADY_CREATED_ORGANITION")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                        "message", "이미 생성한 조직이 존재합니다. 한 계정 당 하나의 조직만 생성할 수 있습니다."
                ));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "조직 생성 중 서버 에러가 발생했습니다."
            ));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{id}/update")
    public ResponseEntity<Map<String, Object>> updateOrganization(
            @AuthenticationPrincipal Long currenUserId,
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrganizationRequest request
    ) {
        try {
            Organization updateOrg = organizationService.updateOrganization(id, currenUserId, request);
            return ResponseEntity.ok(Map.of(
                    "message", "단체가 성공적으로 수정되었습니다.",
                    "data", OrganizationResponseDTO.OrganizationResponse.from(updateOrg)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("ORGANIZATION_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "message", "조직을 찾을 수 없습니다."
                ));
            }
            if (e.getMessage().equals("NOT_ORGANIZATION_OWNER")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "message", "조직 관리 권한이 없습니다."
                ));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "단체 수정 중 서버 에러가 발생했습니다."
            ));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{id}/delete")
    public ResponseEntity<Map<String, Object>> deleteOrganization(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long id
    ) {
        try {
            organizationService.deleteOrganization(id, currentUserId);
            return ResponseEntity.ok(Map.of("message", "조직이 성공적으로 삭제되었습니다."));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("ORGANIZATOIN_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "message", "조직을 찾을 수 없습니다."
                ));
            }
            if (e.getMessage().equals("NOT_ORGANIZATION_OWNER")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "message", "조직 관리 권한이 없습니다."
                ));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "조직 삭제 중 서버 에러가 발생했습니다."
            ));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/join")
    public ResponseEntity<Map<String, Object>> joinOrganization(
            @AuthenticationPrincipal Long currentUserId,
            @Valid @RequestBody JoinOrganizationRequest request
    ) {
        try {
            Member newMember = organizationService.joinOrganization(currentUserId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "단체 가입 신청이 완료되었습니다. 관리자 승인을 기다려주세요.",
                    "data", OrganizationResponseDTO.MemberDto.from(newMember)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("ALREADY_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "message", "올바르지 않은 초대 코드입니다."
                ));
            }
            if (e.getMessage().equals("DEPARTMENT_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "message", "입력한 부서를 찾을 수 없습니다."
                ));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "단체 가입 신청 중 서버 에러가 발생했습니다."
            ));
        }
    }
}
