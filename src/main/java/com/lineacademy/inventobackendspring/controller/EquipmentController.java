package com.lineacademy.inventobackendspring.controller;

import com.lineacademy.inventobackendspring.domain.equipment.Equipment;
import com.lineacademy.inventobackendspring.dto.equipment.request.CreateEquipmentRequest;
import com.lineacademy.inventobackendspring.dto.equipment.request.UpdateEquipmentRequest;
import com.lineacademy.inventobackendspring.dto.equipment.response.EquipmentResponseDTO;
import com.lineacademy.inventobackendspring.service.EquipmentService;
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
@RequestMapping("/equipment")
@RequiredArgsConstructor
public class EquipmentController {
    private final EquipmentService equipmentService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getEquipmentList(
            @AuthenticationPrincipal Long currentUserId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search
    ) {
        try {
            List<Equipment> equipments = equipmentService.getEquipmentList(currentUserId, category, search);

            List<EquipmentResponseDTO.EquipmentListResponse> responseData = equipments.stream()
                    .map(EquipmentResponseDTO.EquipmentListResponse::from)
                    .toList();

            return ResponseEntity.ok(Map.of(
                    "message", "장비 목록을 조회했습니다.",
                    "data", responseData
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "장비 목록 조회 중 서버 에러가 발생했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{equipmentId}")
    public ResponseEntity<Map<String, Object>> getEquipmentById(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long equipmentId
    ) {
        try {
            Equipment equipment = equipmentService.getEquipmentById(currentUserId, equipmentId);
            return ResponseEntity.ok(Map.of(
                    "message", "장비 상세 정보를 조회했습니다.",
                    "data", EquipmentResponseDTO.EquipmentDetailResponse.from(equipment)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("EQUIPMENT_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "장비를 찾을 수 없습니다."));
            }
            if (e.getMessage().equals("EQUIPMENT_NOT_IN_ORGANIZARION_OR_DEPARTMENT")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "조직이나 부서 내에 있는 장비가 아닙니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "장비 상세 조회 중 서버 에러가 발생했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<Map<String, Object>> createEquipment(
            @AuthenticationPrincipal Long currentUserId,
            @Valid @RequestBody CreateEquipmentRequest request
    ) {
        try {
            Equipment newEquipment = equipmentService.createEquipment(currentUserId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "장비가 등록되었습니다.",
                    "data", EquipmentResponseDTO.EquipmentDetailResponse.from(newEquipment)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("CANNOT_CREATE_EQUIPMENT_MEMBER")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "message", "일반 사용자는 장비 등록 권한이 없습니다."
                ));
            }
            if (e.getMessage().equals("DEPARTMENT_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "message", "선택한 부서를 찾을 수 없습니다."
                ));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", "장비 등록 중 서버 에러가 발생했습니다."
                    ));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{equipmentId}")
    public ResponseEntity<Map<String, Object>> updateEquipment(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long equipmentId,
            @Valid @RequestBody UpdateEquipmentRequest request
    ) {
        try {
            Equipment updatedEquipment = equipmentService.updateEquipment(currentUserId, equipmentId, request);
            return ResponseEntity.ok(Map.of(
                    "message", "장비 정보 수정이 완료되었습니다.",
                    "data", EquipmentResponseDTO.EquipmentDetailResponse.from(updatedEquipment)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("EQUIPMENT_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "message", "장비 내역을 찾을 수 없거나 권한이 없습니다."
                ));
            }
            if (e.getMessage().equals("CANNOT_UPDATE_EQUIPMENT_MEMBER")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "message", "일반 사용자는 장비 정보 수정 권한이 없습니다."
                ));
            }
            if (e.getMessage().equals("DEPARTMENT_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "message", "선택한 부서를 찾을 수 없습니다."
                ));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", "장비 정보 수정 중 서버 에러가 발생했습니다."
                    ));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{equipmentId}")
    public ResponseEntity<Map<String, Object>> deleteEquipment(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long equipmentId
    ) {
        try {
            equipmentService.deleteEquipment(currentUserId, equipmentId);
            return ResponseEntity.ok(Map.of("message", "장비 삭제가 완료되었습니다."));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("EQUIPMENT_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "message", "장비 내역을 찾을 수 없거나 권한이 없습니다."
                ));
            }
            if (e.getMessage().equals("CANNOT_DELETE_EQUIPMENT_MEMBER")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "message", "일반 사용자는 장비 삭제 권한이 없습니다."
                ));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "장비 삭제 중 서버 에러가 발생했습니다."));
        }
    }
}
