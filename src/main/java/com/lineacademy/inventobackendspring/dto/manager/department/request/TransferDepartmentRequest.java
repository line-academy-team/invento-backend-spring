package com.lineacademy.inventobackendspring.dto.manager.department.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TransferDepartmentRequest {
    @NotEmpty(message = "이동할 회원 ID 목록을 하나 이상 입력해주세요.")
    private List<Long> memberIds;

    @NotNull(message = "이동할 부서 ID를 입력해주세요.")
    private Long targetDepartmentId;
}