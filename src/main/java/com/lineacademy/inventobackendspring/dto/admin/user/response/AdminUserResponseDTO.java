package com.lineacademy.inventobackendspring.dto.admin.user.response;

import com.lineacademy.inventobackendspring.domain.enums.UserRole;
import com.lineacademy.inventobackendspring.domain.member.Member;
import com.lineacademy.inventobackendspring.domain.organization.Organization;
import com.lineacademy.inventobackendspring.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;



public class AdminUserResponseDTO {

    @Getter
    @Builder
    public static class AdminUserResponse {
        private Long id;
        private String email;
        private String name;
        private UserRole role;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime deletedAt;

        public static AdminUserResponse from(User user) {
            return AdminUserResponse.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .role(user.getRole())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .deletedAt(user.getDeletedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class AdminUserDetailResponse {
        private Long id;
        private String email;
        private String name;
        private UserRole role;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime deletedAt;

        private List<OrganizationSummary> createdOrganizations;
        private MemberSummary member;

        public static AdminUserDetailResponse from(User user) {
            return AdminUserDetailResponse.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .role(user.getRole())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .deletedAt(user.getDeletedAt())
                    .createdOrganizations(user.getOrganizations() != null ?
                            user.getOrganizations().stream()
                                    .map(OrganizationSummary::from)
                                    .collect(Collectors.toList()) : null)
                    .member(MemberSummary.from(user.getMember()))
                    .build();
        }
    }

    @Getter
    @Builder
    public static class OrganizationSummary {
        private Long id;
        private String name;

        public static OrganizationSummary from(Organization org) {
            return OrganizationSummary.builder()
                    .id(org.getId())
                    .name(org.getName())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class MemberSummary {
        private Long id;
        private String role;
        private String status;

        public static MemberSummary from(Member member) {
            if (member == null) return null;
            return MemberSummary.builder()
                    .id(member.getId())
                    .role(member.getRole().name())
                    .status(member.getStatus().name())
                    .build();
        }
    }
}
