package com.lineacademy.inventobackendspring.dto.equipment.response;

import com.lineacademy.inventobackendspring.domain.equipment.Equipment;
import com.lineacademy.inventobackendspring.domain.equipmentunit.EquipmentUnit;
import com.lineacademy.inventobackendspring.domain.enums.EquipmentStatus;
import com.lineacademy.inventobackendspring.domain.enums.EquipmentType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class EquipmentResponseDTO {
    @Getter
    @Builder
    public static class EquipmentListResponse {
        private Long id;
        private String name;
        private String category;
        private String imageUrl;
        private EquipmentType type;
        private Integer totalQuantity;
        private Integer availableQuantity;
        private EquipmentStatus status;
        private DepartmentSummary department;
        private LocalDateTime createdAt;

        public static EquipmentListResponse from(Equipment equipment) {
            return EquipmentListResponse.builder()
                    .id(equipment.getId())
                    .name(equipment.getName())
                    .category(equipment.getCategory())
                    .imageUrl(equipment.getImageUrl())
                    .type(equipment.getType())
                    .totalQuantity(equipment.getTotalQuantity())
                    // 수정: getAvailableQuantity()로 변경
                    .availableQuantity(equipment.getAvailableQuantity())
                    .status(equipment.getStatus())
                    .department(DepartmentSummary.from(equipment.getDepartment()))
                    .createdAt(equipment.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class EquipmentDetailResponse {
        private Long id;
        private String name;
        private String category;
        private String description;
        private String imageUrl;
        private EquipmentType type;
        private Integer totalQuantity;
        private Integer availableQuantity;
        private EquipmentStatus status;
        private DepartmentSummary department;
        private List<EquipmentUnitDto> units;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static EquipmentDetailResponse from(Equipment equipment) {
            return EquipmentDetailResponse.builder()
                    .id(equipment.getId())
                    .name(equipment.getName())
                    .category(equipment.getCategory())
                    .description(equipment.getDescription())
                    .imageUrl(equipment.getImageUrl())
                    .type(equipment.getType())
                    .totalQuantity(equipment.getTotalQuantity())
                    // 추가: availableQuantity 매핑 추가
                    .availableQuantity(equipment.getAvailableQuantity())
                    .status(equipment.getStatus())
                    .department(DepartmentSummary.from(equipment.getDepartment()))
                    .units(equipment.getUnits() != null ? equipment.getUnits().stream()
                            .map(EquipmentUnitDto::from)
                            .collect(Collectors.toList()) : List.of())
                    .createdAt(equipment.getCreatedAt())
                    .updatedAt(equipment.getUpdatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class DepartmentSummary {
        private Long id;
        private String name;

        public static DepartmentSummary from(com.lineacademy.inventobackendspring.domain.department.Department dept) {
            if (dept == null) return null;
            return DepartmentSummary.builder()
                    .id(dept.getId())
                    .name(dept.getName())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class EquipmentUnitDto {
        private Long id;
        private String assetNumber;
        private EquipmentStatus status;

        public static EquipmentUnitDto from(EquipmentUnit unit) {
            return EquipmentUnitDto.builder()
                    .id(unit.getId())
                    .assetNumber(unit.getAssetNumber())
                    .status(unit.getStatus())
                    .build();
        }
    }
}