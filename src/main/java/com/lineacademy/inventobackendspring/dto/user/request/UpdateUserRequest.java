package com.lineacademy.inventobackendspring.dto.user.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
    @Size(min = 1, max = 50, message = "이름을 입력해주세요.")
    private String name;

    @Pattern(regexp = "^http?://.*", message = "올바른 USL(http/https) 형식이 아닙니다.")
    private String imageUrl;
}
