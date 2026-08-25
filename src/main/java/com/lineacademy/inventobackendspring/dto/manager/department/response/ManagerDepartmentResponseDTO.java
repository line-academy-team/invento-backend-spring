package com.lineacademy.inventobackendspring.dto.manager.department.response;

import com.lineacademy.inventobackendspring.domain.department.Department;
import com.lineacademy.inventobackendspring.domain.enums.MemberRole;
import com.lineacademy.inventobackendspring.domain.member.Member;
import com.lineacademy.inventobackendspring.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

public class ManagerDepartmentResponseDTO {

    @Getter
    @Builder
    public static class OrgMemberResponse {
        private Long id;
        private MemberRole role;
        private UserSummary user;
        private DepartmentSummary department;

        public static OrgMemberResponse from(Member member) {
            return OrgMemberResponse.builder()
                    .id(member.getId())
                    .role(member.getRole())
                    .user(UserSummary.from(member.getUser()))
                    .department(DepartmentSummary.from(member.getDepartment()))
                    .build();
        }
    }

    @Getter
    @Builder
    public static class DepartmentDetailResponse {
        private Long id;
        private String name;
        private String description;
        private List<OrgMemberResponse> members;

        public static DepartmentDetailResponse from(Department department) {
            return DepartmentDetailResponse.builder()
                    .id(department.getId())
                    .name(department.getName())
                    .description(department.getDescription())
                    .members(department.getMembers().stream()
                            .map(OrgMemberResponse::from)
                            .collect(Collectors.toList()))
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
    public static class DepartmentSummary {
        private Long id;
        private String name;

        public static DepartmentSummary from(Department department) {
            if (department == null) return null;
            return DepartmentSummary.builder()
                    .id(department.getId())
                    .name(department.getName())
                    .build();
        }
    }
}