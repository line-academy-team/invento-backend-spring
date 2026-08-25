package com.lineacademy.inventobackendspring.dto.equipmentunit.response;

import com.lineacademy.inventobackendspring.domain.equipmentunit.EquipmentUnit;
import com.lineacademy.inventobackendspring.domain.enums.EquipmentsStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class EquipmentUnitResponseDTO {

    @Getter
    @Builder
    public static class EquipmentUnitResponse {
        private Long id;
        private Long equipmentId;
        private String assetNumber;
        private EquipmentsStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static EquipmentUnitResponse from(EquipmentUnit unit) {
            return EquipmentUnitResponse.builder()
                    .id(unit.getId())
                    .equipmentId(unit.getEquipment().getId())
                    .assetNumber(unit.getAssetNumber())
                    .status(unit.getStatus())
                    .createdAt(unit.getCreatedAt())
                    .updatedAt(unit.getUpdatedAt())
                    .build();
        }
    }
}
