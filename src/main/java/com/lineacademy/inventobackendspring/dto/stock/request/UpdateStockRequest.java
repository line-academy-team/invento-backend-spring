package com.lineacademy.inventobackendspring.dto.stock.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStockRequest {
    @Min(value = 1, message = "요청 수량은 1 이상이어야 합니다.")
    private Integer quantity;

    private String reason;
}
