package com.lineacademy.inventobackendspring.dto.admin.organization.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUpdateOrganizationRequest {
    @Size(min = 1, max = 100, message = "조직명을 1~100자 이내로 입력해주세요.")
    private String name;

    private String description;

    private Boolean isSuspended;
}
