package com.lineacademy.inventobackendspring.controller.manager;

import com.lineacademy.inventobackendspring.domain.department.Department;
import com.lineacademy.inventobackendspring.domain.enums.MemberStatus;
import com.lineacademy.inventobackendspring.domain.member.Member;
import com.lineacademy.inventobackendspring.dto.manager.member.request.ProcessJoinRequest;
import com.lineacademy.inventobackendspring.dto.manager.member.response.ManagerJoinResponseDTO;
import com.lineacademy.inventobackendspring.service.manager.ManagerJoinRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/manager/join")
@RequiredArgsConstructor
public class ManagerJoinRequestController {

    private final ManagerJoinRequestService joinRequestService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getJoinRequestList(
            @AuthenticationPrincipal Long currentUserId,
            @RequestParam(required = false) String search
    ) {
        try {
            List<Member> joinRequests = joinRequestService.getJoinRequestList(currentUserId, search);

            List<ManagerJoinResponseDTO.JoinRequestListResponse> responseData = joinRequests.stream()
                    .map(ManagerJoinResponseDTO.JoinRequestListResponse::from)
                    .toList();

            return ResponseEntity.ok(Map.of(
                    "message", "조직 가입 요청 목록을 불러왔습니다.",
                    "data", responseData
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("FORBIDDEN_APPROVAL")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "해당 요청을 처리할 권한이 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "가입 요청 목록 조회 중 오류가 발생했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{requesterId}")
    @SuppressWarnings("unchecked")
    public ResponseEntity<Map<String, Object>> getJoinRequestById(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long requesterId
    ) {
        try {
            Object[] result = joinRequestService.getJoinRequestById(currentUserId, requesterId);
            Member joinRequest = (Member) result[0];
            List<Department> departments = (List<Department>) result[1];

            return ResponseEntity.ok(Map.of(
                    "message", "조직 가입 요청 상세 정보를 불러왔습니다.",
                    "data", ManagerJoinResponseDTO.JoinRequestDetailResponse.from(joinRequest, departments)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("FORBIDDEN_APPROVAL")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "해당 요청을 처리할 권한이 없습니다."));
            }
            if (e.getMessage().equals("JOIN_REQUEST_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 가입 신청 내역을 찾을 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "가입 요청 상세 조회 중 오류가 발생했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/process")
    public ResponseEntity<Map<String, Object>> processJoinOrganization(
            @AuthenticationPrincipal Long currentUserId,
            @Valid @RequestBody ProcessJoinRequest request
    ) {
        try {
            joinRequestService.processJoinOrganization(currentUserId, request);

            String statusMessage = request.getStatus() == MemberStatus.APPROVED ? "승인" : "반려";
            return ResponseEntity.ok(Map.of(
                    "message", "가입 요청 " + statusMessage + " 처리가 완료되었습니다."
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("FORBIDDEN_APPROVAL")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "해당 요청을 처리할 권한이 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "가입 요청 처리 중 오류가 발생했습니다."));
        }
    }
}