package com.lineacademy.inventobackendspring.dto.rental.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateRentalRequest {
    @Min(value = 1, message = "대여 수량은 1 이상이어야 합니다.")
    private Integer quantity;

    @Size(max = 255, message = "대여 사유를 작성해주세요.")
    private String reason;

    private LocalDateTime dueAt;
}
