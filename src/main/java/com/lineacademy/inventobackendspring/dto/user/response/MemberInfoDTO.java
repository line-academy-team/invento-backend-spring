package com.lineacademy.inventobackendspring.dto.user.response;

import com.lineacademy.inventobackendspring.domain.enums.MemberRole;
import com.lineacademy.inventobackendspring.domain.enums.MemberStatus;
import com.lineacademy.inventobackendspring.domain.member.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemberInfoDTO {
    private Long id;
    private Long organizationId;
    private String organizationName;
    private Long departmentId;
    private String departmentName;
    private MemberRole role;
    private MemberStatus status;
    private LocalDateTime joinedAt;

    public static MemberInfoDTO from(Member member) {
        if (member == null) return null;

        return MemberInfoDTO.builder()
                .id(member.getId())
                .organizationId(member.getOrganization().getId())
                .organizationName(member.getOrganization().getName())
                .departmentId(member.getDepartment() != null ?
                        member.getDepartment().getId() : null)
                .departmentName(member.getDepartment() != null ?
                        member.getDepartment().getName() : null)
                .role(member.getRole())
                .status(member.getStatus())
                .joinedAt(member.getJoinedAt())
                .build();
    }
}
