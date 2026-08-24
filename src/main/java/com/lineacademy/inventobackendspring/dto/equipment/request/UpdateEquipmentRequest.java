package com.lineacademy.inventobackendspring.dto.equipment.request;

import com.lineacademy.inventobackendspring.domain.enums.EquipmentType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateEquipmentRequest {
    @Size(max = 100)
    private String name;

    private EquipmentType type;

    @Min(value = 0, message = "수량은 0 이상이어야 합니다.")
    private Integer totalQuantity;

    private Long departmentId;

    @Size(max = 50)
    private String category;

    private String description;

    @Size(max = 255)
    private String imageUrl;
}
