package com.lineacademy.inventobackendspring.dto.owner.department.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignDepartmentManagerRequest {
    @NotNull(message = "회원 ID를 입력해주세요.")
    private Long memberId;
}