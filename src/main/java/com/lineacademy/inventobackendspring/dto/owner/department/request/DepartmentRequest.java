package com.lineacademy.inventobackendspring.dto.owner.department.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentRequest {
    @NotBlank(message = "부서명을 입력해주세요.")
    @Size(max = 50, message = "부서명은 50자 이내여야 합니다.")
    private String name;
}