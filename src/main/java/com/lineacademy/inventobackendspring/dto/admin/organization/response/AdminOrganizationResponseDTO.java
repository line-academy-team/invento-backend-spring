package com.lineacademy.inventobackendspring.dto.admin.organization.response;

import com.lineacademy.inventobackendspring.domain.organization.Organization;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class AdminOrganizationResponseDTO {

    @Getter
    @Builder
    public static class OrganizationDetail {
        private Long id;
        private String name;
        private String description;
        private String logoUrl;
        private String inviteCode;
        private CreatorInfo creator;
        private CountInfo count;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime deletedAt;

        public static OrganizationDetail from(Organization org) {
            return OrganizationDetail.builder()
                    .id(org.getId())
                    .name(org.getName())
                    .description(org.getDescription())
                    .logoUrl(org.getLogoUrl())
                    .inviteCode(org.getInviteCode())
                    .creator(CreatorInfo.from(org.getCreator()))
                    .count(CountInfo.builder()
                            .members(org.getMembers() != null ? (long) org.getMembers().size() : 0L)
                            .equipments(org.getEquipments() != null ? (long) org.getEquipments().size() : 0L)
                            .build())
                    .createdAt(org.getCreatedAt())
                    .updatedAt(org.getUpdatedAt())
                    .deletedAt(org.getDeletedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class CreatorInfo {
        private Long id;
        private String name;
        private String email;

        public static CreatorInfo from(com.lineacademy.inventobackendspring.domain.user.User user) {
            if (user == null) return null;
            return CreatorInfo.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class CountInfo {
        private Long members;
        private Long equipments;
    }
}