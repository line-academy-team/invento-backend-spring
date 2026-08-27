package com.lineacademy.inventobackendspring.controller;

import com.lineacademy.inventobackendspring.domain.report.Report;
import com.lineacademy.inventobackendspring.dto.report.request.CreateReportRequest;
import com.lineacademy.inventobackendspring.dto.report.request.ProcessReportRequest;
import com.lineacademy.inventobackendspring.dto.report.request.UpdateReportRequest;
import com.lineacademy.inventobackendspring.dto.report.response.ReportResponseDTO;
import com.lineacademy.inventobackendspring.service.ReportService;

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
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createReportRequest(
            @AuthenticationPrincipal Long currentUserId,
            @Valid @RequestBody CreateReportRequest request
    ) {
        try {
            Report report = reportService.createReport(currentUserId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "보고 전송이 완료되었습니다. 관리자 승인을 대기해주세요.",
                    "data", ReportResponseDTO.ReportResponse.from(report)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("BORROWED_RENTAL_REQUIRED")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "대여중인 장비만 파손 신고할 수 있습니다."));
            }
            if (e.getMessage().equals("PENDING_REPORT_ALREADY_EXISTS")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "이미 처리 대기 중인 파손 신고가 있습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "보고 전송 중 서버 에러가 발생했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getReportList(
            @AuthenticationPrincipal Long currentUserId,
            @RequestParam(required = false) Long ozId
    ) {
        try {
            List<Report> reports = reportService.getReportList(currentUserId, ozId);
            String message = ozId != null ? "조직 보고 목록을 불러왔습니다." : "내 보고 목록을 불러왔습니다.";
            return ResponseEntity.ok(Map.of(
                    "message", message,
                    "data", reports.stream().map(ReportResponseDTO.ReportResponse::from).collect(Collectors.toList())
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("MEMBER_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "소속된 단체 멤버 정보를 찾을 수 없습니다."));
            }
            if (e.getMessage().equals("MANAGER_PERMISSION_REQUIRED")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "보고 관리 권한이 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "보고 전송 목록을 불러오는 중 서버 에러가 발생했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{reportId}")
    public ResponseEntity<Map<String, Object>> getReportById(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long reportId
    ) {
        try {
            Report report = reportService.getReportById(currentUserId, reportId);
            return ResponseEntity.ok(Map.of(
                    "message", "보고 내용을 불러왔습니다.",
                    "data", ReportResponseDTO.ReportResponse.from(report)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("REPORT_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "보고 내용을 찾을 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "보고 내용을 불러오는 중 서버 에러가 발생했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{reportId}/process")
    public ResponseEntity<Map<String, Object>> processReport(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long reportId,
            @Valid @RequestBody ProcessReportRequest request
    ) {
        try {
            Report report = reportService.processReport(currentUserId, reportId, request);
            return ResponseEntity.ok(Map.of(
                    "message", "보고 처리가 완료되었습니다.",
                    "data", ReportResponseDTO.ReportResponse.from(report)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("MANAGER_PERMISSION_REQUIRED")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "보고 관리 권한이 없습니다."));
            }
            if (e.getMessage().equals("REPORT_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "보고 내용을 찾을 수 없습니다."));
            }
            if (e.getMessage().equals("REPORT_ALREADY_PROCESSED")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "이미 처리된 보고입니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "보고 처리 중 서버 에러가 발생했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{reportId}")
    public ResponseEntity<Map<String, Object>> updateReport(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long reportId,
            @Valid @RequestBody UpdateReportRequest request
    ) {
        try {
            Report updatedReport = reportService.updateReport(currentUserId, reportId, request);
            return ResponseEntity.ok(Map.of(
                    "message", "보고 수정이 완료되었습니다.",
                    "data", ReportResponseDTO.ReportResponse.from(updatedReport)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("REPORT_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "보고 내역을 찾을 수 없거나 권한이 없습니다."));
            }
            if (e.getMessage().equals("CANNOT_UPDATE_COMPLETED_REPORT")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "처리 완료된 보고는 수정할 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "보고 수정 중 서버 에러가 발생했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{reportId}")
    public ResponseEntity<Map<String, Object>> deleteReport(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long reportId
    ) {
        try {
            reportService.deleteReport(currentUserId, reportId);
            return ResponseEntity.ok(Map.of("message", "보고 취소가 완료되었습니다."));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("REPORT_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "보고 내역을 찾을 수 없거나 권한이 없습니다."));
            }
            if (e.getMessage().equals("CANNOT_CANCEL_COMPLETED_REPORT")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "처리 완료된 보고는 취소할 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "보고 취소 중 서버 에러가 발생했습니다."));
        }
    }
}