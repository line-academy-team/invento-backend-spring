package com.lineacademy.inventobackendspring.dto.manager.member.request;

import com.lineacademy.inventobackendspring.domain.enums.MemberStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProcessJoinRequest {
    @NotEmpty(message = "처리할 회원 ID를 하나 이상 선택해주세요.")
    private List<Long> memberIds;

    @NotNull(message = "처리 상태를 선택해주세요.")
    private MemberStatus status;

    private Long departmentId;

    private String rejectedReason;
}