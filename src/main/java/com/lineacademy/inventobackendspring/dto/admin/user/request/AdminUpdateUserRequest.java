package com.lineacademy.inventobackendspring.dto.admin.user.request;

import com.lineacademy.inventobackendspring.domain.enums.UserRole;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUpdateUserRequest {
    @Size(max = 50, message = "이름은 최대 50자까지 입력 가능합니다.")
    private String name;

    private UserRole role;

    private Boolean isDeleted;
}
