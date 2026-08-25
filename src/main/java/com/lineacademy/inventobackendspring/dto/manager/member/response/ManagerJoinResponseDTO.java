package com.lineacademy.inventobackendspring.dto.manager.member.response;

import com.lineacademy.inventobackendspring.domain.department.Department;
import com.lineacademy.inventobackendspring.domain.enums.MemberStatus;
import com.lineacademy.inventobackendspring.domain.member.Member;
import com.lineacademy.inventobackendspring.domain.organization.Organization;
import com.lineacademy.inventobackendspring.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ManagerJoinResponseDTO {

    @Getter
    @Builder
    public static class JoinRequestListResponse {
        private Long id;
        private MemberStatus status;
        private LocalDateTime createdAt;
        private UserSummary user;
        private DepartmentSummary department;

        public static JoinRequestListResponse from(Member member) {
            return JoinRequestListResponse.builder()
                    .id(member.getId())
                    .status(member.getStatus())
                    .createdAt(member.getCreatedAt())
                    .user(UserSummary.from(member.getUser()))
                    .department(DepartmentSummary.from(member.getDepartment()))
                    .build();
        }
    }

    @Getter
    @Builder
    public static class JoinRequestDetailResponse {
        private Long id;
        private MemberStatus status;
        private LocalDateTime createdAt;
        private UserSummary user;
        private OrganizationSummary organization;
        private DepartmentSummary department;
        private List<DepartmentSummary> departments; // 선택 가능한 부서 목록

        public static JoinRequestDetailResponse from(Member member, List<Department> departments) {
            return JoinRequestDetailResponse.builder()
                    .id(member.getId())
                    .status(member.getStatus())
                    .createdAt(member.getCreatedAt())
                    .user(UserSummary.from(member.getUser()))
                    .organization(OrganizationSummary.from(member.getOrganization()))
                    .department(DepartmentSummary.from(member.getDepartment()))
                    .departments(departments.stream().map(DepartmentSummary::from).collect(Collectors.toList()))
                    .build();
        }
    }

    @Getter
    @Builder
    public static class UserSummary {
        private Long id;
        private String name;
        private String email;

        public static UserSummary from(User user) {
            if (user == null) return null;
            return UserSummary.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class OrganizationSummary {
        private String name;

        public static OrganizationSummary from(Organization org) {
            if (org == null) return null;
            return OrganizationSummary.builder()
                    .name(org.getName())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class DepartmentSummary {
        private Long id;
        private String name;

        public static DepartmentSummary from(Department dept) {
            if (dept == null) return null;
            return DepartmentSummary.builder()
                    .id(dept.getId())
                    .name(dept.getName())
                    .build();
        }
    }
}