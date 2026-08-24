package com.lineacademy.inventobackendspring.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WithdrawUserRequest {
    @NotBlank(message = "탈퇴를 위해 비밀번호를 입력해주세요.")
    private String password;

    @Size(max = 255)
    private String reason;
}
