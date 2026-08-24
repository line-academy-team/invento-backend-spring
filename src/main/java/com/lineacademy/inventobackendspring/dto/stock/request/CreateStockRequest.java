package com.lineacademy.inventobackendspring.dto.stock.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateStockRequest {
    @NotBlank(message = "장비 ID를 선택해주세요")
    private Long equipmentId;

    @NotBlank(message = "요청 수량을 입력해주세요.")
    @Min(value = 1, message = "요청 수량은 1 이상이어야 합니다.")
    private Integer quantity;

    private String reason;
}
