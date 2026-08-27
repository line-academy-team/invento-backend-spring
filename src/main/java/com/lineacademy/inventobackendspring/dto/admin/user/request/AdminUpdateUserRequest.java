package com.lineacademy.inventobackendspring.dto.admin.user.request;

import com.lineacademy.inventobackendspring.domain.enums.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUpdateUserRequest {
    private UserRole role;

    private Boolean isDeleted;
}
