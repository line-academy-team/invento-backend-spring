package com.lineacademy.inventobackendspring.dto.equipmentunit.request;

import com.lineacademy.inventobackendspring.domain.enums.EquipmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateEquipmentUnitRequest {
    @NotNull(message = "장비 ID를 입력해주세요.")
    private Long equipmentId;

    @NotBlank(message = "자산 번호(식별 번호)를 입력해주세요.")
    @Size(max = 50)
    private String assetNumber;

    private EquipmentStatus status;
}
