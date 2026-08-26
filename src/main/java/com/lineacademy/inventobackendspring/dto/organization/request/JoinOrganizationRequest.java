package com.lineacademy.inventobackendspring.dto.organization.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinOrganizationRequest {
    @NotBlank(message = "초대 코드를 입력해주세요.")
    @Size(max = 20)
    private String inviteCode;

    private String department;
}
