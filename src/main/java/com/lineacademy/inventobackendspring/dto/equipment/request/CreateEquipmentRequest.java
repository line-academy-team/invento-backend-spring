package com.lineacademy.inventobackendspring.dto.equipment.request;

import com.lineacademy.inventobackendspring.domain.enums.EquipmentType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateEquipmentRequest {
    @NotBlank(message = "장비 이름을 입력해주세요.")
    @Size(max = 100)
    private String name;

    @NotNull(message = "장비 타입을 선택해주세요.")
    private EquipmentType type;

    @NotNull(message = "전체 수량을 입력해주세요.")
    @Min(value = 0, message = "수량은 0 이상이어야 합니다.")
    private Integer totalQuantity;

    private Long departmentId;

    @Size(max = 50)
    private String category;

    private String description;

    @Size(max = 255)
    private String imageUrl;
}
