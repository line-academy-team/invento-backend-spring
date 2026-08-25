package com.lineacademy.inventobackendspring.dto.admin.organization.response;

import com.lineacademy.inventobackendspring.domain.organization.Organization;
import com.lineacademy.inventobackendspring.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class AdminOrganizationResponseDTO {

    @Getter
    @Builder
    public static class AdminOrganizationResponse {
        private Long id;
        private String name;
        private String description;
        private String logoUrl;
        private String inviteCode;
        private Long createdBy;
        private CreatorSummary creator;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime deletedAt;

        private int memberCount;
        private int equipmentCount;

        public static AdminOrganizationResponse from(Organization org) {
            return AdminOrganizationResponse.builder()
                    .id(org.getId())
                    .name(org.getName())
                    .description(org.getDescription())
                    .logoUrl(org.getLogoUrl())
                    .inviteCode(org.getInviteCode())
                    .createdBy(org.getCreator().getId())
                    .creator(CreatorSummary.from(org.getCreator()))
                    .createdAt(org.getCreatedAt())
                    .updatedAt(org.getUpdatedAt())
                    .deletedAt(org.getDeletedAt())
                    .memberCount(org.getMembers() != null ? org.getMembers().size() : 0)
                    .equipmentCount(org.getEquipments() != null ? org.getEquipments().size() : 0)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class CreatorSummary {
        private Long id;
        private String name;
        private String email;

        public static CreatorSummary from(User user) {
            if (user == null) return null;
            return CreatorSummary.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .build();
        }
    }
}
