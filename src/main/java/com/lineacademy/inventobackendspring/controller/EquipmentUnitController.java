package com.lineacademy.inventobackendspring.controller;

import com.lineacademy.inventobackendspring.domain.equipmentunit.EquipmentUnit;
import com.lineacademy.inventobackendspring.dto.equipmentunit.request.UpdateEquipmentUnitRequest;
import com.lineacademy.inventobackendspring.dto.equipmentunit.request.CreateEquipmentUnitRequest;
import com.lineacademy.inventobackendspring.dto.equipmentunit.response.EquipmentUnitResponseDTO;
import com.lineacademy.inventobackendspring.service.EquipmentUnitService;
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
@RequestMapping("/equipment-unit")
@RequiredArgsConstructor
public class EquipmentUnitController {

    private final EquipmentUnitService equipmentUnitService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{equipmentId}")
    public ResponseEntity<Map<String, Object>> getUnitByEquipmentId(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long equipmentId
    ) {
        try {
            List<EquipmentUnit> units = equipmentUnitService.getUnitsByEquipmentId(currentUserId, equipmentId);

            List<EquipmentUnitResponseDTO.EquipmentUnitResponse> responseData = units.stream()
                    .map(EquipmentUnitResponseDTO.EquipmentUnitResponse::from)
                    .toList();

            return ResponseEntity.ok(Map.of(
                    "message", "장비 유닛 목록을 조회했습니다.",
                    "data", responseData
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("UNIT_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "message", "장비 유닛을 찾을 수 없습니다."
                ));
            }
            if (e.getMessage().equals("EQUIPMENTUNIT_NOT_IN_ORGANIZATION_OR_DEPARTMENT")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "message", "조직이나 부서 내에 있는 장비 유닛이 아닙니다."
                ));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", "장비 유닛 조회 중 서버 에러가 발생했습니다."
                    ));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<Map<String, Object>> createUnit(
            @AuthenticationPrincipal Long currentUserId,
            @Valid @RequestBody CreateEquipmentUnitRequest request
    ) {
        try {
            EquipmentUnit newUnit = equipmentUnitService.createEquipmentUnit(currentUserId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "장비 유닛이 등록되었습니다.",
                    "data", EquipmentUnitResponseDTO.EquipmentUnitResponse.from(newUnit)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("CANNOT_CREATE_EQUIPMENTUNIT_MEMBER")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "일반 사용자는 장비 유닛 등록 권한이 없습니다."));
            }
            if (e.getMessage().equals("UNIT_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 장비를 찾을 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "장비 유닛 등록 중 서버 에러가 발생했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{unitId}")
    public ResponseEntity<Map<String, Object>> updateUnit(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long unitId,
            @Valid @RequestBody UpdateEquipmentUnitRequest request
    ) {
        try {
            EquipmentUnit updatedUnit = equipmentUnitService.updateEquipmentUnit(currentUserId, unitId, request);
            return ResponseEntity.ok(Map.of(
                    "message", "장비 유닛 정보가 수정되었습니다.",
                    "data", EquipmentUnitResponseDTO.EquipmentUnitResponse.from(updatedUnit)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("UNIT_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "message", "장비 유닛을 찾을 수 없습니다."
                ));
            }
            if (e.getMessage().equals("CANNOT_UPDATE_EQUIPMENTUNIT_MEMBER")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "message", "일반 사용자는 장비 유닛 수정 권한이 없습니다."
                ));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "장비 유닛 수정 중 서버 에러가 발생했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{unitId}")
    public ResponseEntity<Map<String, Object>> deleteUnit(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long unitId
    ) {
        try {
            equipmentUnitService.deleteEquipmentUnit(currentUserId, unitId);
            return ResponseEntity.ok(Map.of("message", "장비 유닛이 삭제되었습니다."));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("UNIT_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "message", "장비 유닛을 찾을 수 없습니다."
                ));
            }
            if (e.getMessage().equals("CANNOT_DELETE_EQUIPMENTUNIT_MEMBER")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "message", "일반 사용자는 장비 유닛 삭제 권한이 없습니다."
                ));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "장비 유닛 삭제 중 서버 에러가 발생했습니다."));
        }
    }
}
