package com.lineacademy.inventobackendspring.dto.organization.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrganizationRequest {
    @NotBlank(message = "조직명을 입력해주세요.")
    @Size(max = 100)
    private String name;

    @Size(max = 500, message = "소개글은 최대 500자 입니다.")
    private String description;

    @Size(max = 255)
    private String logoUrl;
}
