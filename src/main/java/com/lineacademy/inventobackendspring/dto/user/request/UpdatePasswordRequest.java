package com.lineacademy.inventobackendspring.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordRequest {
    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String currentPassword;

    @NotBlank(message = "새 비밀번호는 최소 6자 이상이어야 합니다.")
    @Size(min = 6, max = 255, message = "새 비밀번호는 최소 6자 이상이어야 합니다.")
    private String newPassword;

}
