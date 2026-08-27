package com.lineacademy.inventobackendspring.dto.admin.organization.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUpdateOrganizationRequest {
    @Size(max = 100, message = "조직명은 최대 100자입니다.")
    private String name;

    @Size(max = 500, message = "소개글은 최대 500자입니다.")
    private String description;

    private Boolean isSuspended;
}
