package com.lineacademy.inventobackendspring.dto.owner.department.response;

import com.lineacademy.inventobackendspring.domain.department.Department;
import com.lineacademy.inventobackendspring.domain.enums.MemberRole;
import com.lineacademy.inventobackendspring.domain.member.Member;
import com.lineacademy.inventobackendspring.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OwnerDepartmentResponseDTO {

    @Getter
    @Builder
    public static class DepartmentListResponse {
        private Long id;
        private String name;
        private LocalDateTime createdAt;
        private List<DepartmentMemberSummary> members;

        public static DepartmentListResponse from(Department department) {
            return DepartmentListResponse.builder()
                    .id(department.getId())
                    .name(department.getName())
                    .createdAt(department.getCreatedAt())
                    .members(department.getMembers().stream()
                            .map(DepartmentMemberSummary::from)
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    @Getter
    @Builder
    public static class DepartmentMemberSummary {
        private Long id;
        private MemberRole role;
        private UserSummary user;

        public static DepartmentMemberSummary from(Member member) {
            return DepartmentMemberSummary.builder()
                    .id(member.getId())
                    .role(member.getRole())
                    .user(UserSummary.from(member.getUser()))
                    .build();
        }
    }

    @Getter
    @Builder
    public static class UserSummary {
        private Long id;
        private String name;

        public static UserSummary from(User user) {
            if (user == null) return null;
            return UserSummary.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .build();
        }
    }
}