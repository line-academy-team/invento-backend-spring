package com.lineacademy.inventobackendspring.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "유효한 이메일을 입력해주세요.")
    @Size(max = 100)
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(max = 255)
    private String password;

    @NotBlank(message = "이름을 입력해주세요.")
    @Size(max = 50)
    private String name;
}
