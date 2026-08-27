package com.lineacademy.inventobackendspring.dto.organization.response;

import com.lineacademy.inventobackendspring.domain.department.Department;
import com.lineacademy.inventobackendspring.domain.enums.MemberRole;
import com.lineacademy.inventobackendspring.domain.enums.MemberStatus;
import com.lineacademy.inventobackendspring.domain.member.Member;
import com.lineacademy.inventobackendspring.domain.organization.Organization;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

public class OrganizationResponseDTO {
    @Getter
    @Builder
    public static class OrganizationResponse {
        private Long id;
        private String name;
        private String description;
        private String logoUrl;
        private String inviteCode;
        private Long createdBy;

        public static OrganizationResponse from(Organization org) {
            return OrganizationResponse.builder()
                    .id(org.getId())
                    .name(org.getName())
                    .description(org.getDescription())
                    .logoUrl(org.getLogoUrl())
                    .inviteCode(org.getInviteCode())
                    .createdBy(org.getCreator().getId())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class OrganizationDetailResponse {
        private Long id;
        private String name;
        private String description;
        private String logoUrl;
        private String inviteCode;
        private List<DepartmentDto> departments;
        private List<MemberDto> members;

        public static OrganizationDetailResponse from(Organization org) {
            return OrganizationDetailResponse.builder()
                    .id(org.getId())
                    .name(org.getName())
                    .description(org.getDescription())
                    .logoUrl(org.getLogoUrl())
                    .inviteCode(org.getInviteCode())
                    .departments(org.getDepartments().stream()
                            .map(DepartmentDto::from)
                            .collect(Collectors.toList()))
                    .members(org.getMembers().stream()
                            .filter(m -> m.getStatus() == MemberStatus.PENDING || m.getStatus() == MemberStatus.APPROVED)
                            .map(MemberDto::from)
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    @Getter
    @Builder
    public static class DepartmentDto {
        private Long id;
        private String name;
        private String description;

        public static DepartmentDto from(Department dept) {
            return DepartmentDto.builder()
                    .id(dept.getId())
                    .name(dept.getName())
                    .description(dept.getDescription())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class MemberDto {
        private Long id;
        private Long userId;
        private MemberRole role;
        private String userName;
        private String userEmail;

        public static MemberDto from(Member member) {
            return MemberDto.builder()
                    .id(member.getId())
                    .userId(member.getUser().getId())
                    .role(member.getRole())
                    .userName(member.getUser().getName())
                    .userEmail(member.getUser().getEmail())
                    .build();
        }
    }
}
