package com.lineacademy.inventobackendspring.dto.rental.request;

import com.lineacademy.inventobackendspring.domain.enums.RentalStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcessRentalRequest {
    @NotNull(message = "처리 상태를 선택해주세요.")
    private RentalStatus status;

    private String rejectedReason;
}
