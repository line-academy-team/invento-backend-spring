package com.lineacademy.inventobackendspring.dto.equipmentunit.request;

import com.lineacademy.inventobackendspring.domain.enums.EquipmentStatus;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateEquipmentUnitRequest {
    @Size(max = 50)
    private String assetNumber;

    private EquipmentStatus status;
}
