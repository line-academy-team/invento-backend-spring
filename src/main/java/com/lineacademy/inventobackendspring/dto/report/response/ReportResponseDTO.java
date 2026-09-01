package com.lineacademy.inventobackendspring.dto.report.response;

import com.lineacademy.inventobackendspring.domain.department.Department;
import com.lineacademy.inventobackendspring.domain.equipment.Equipment;
import com.lineacademy.inventobackendspring.domain.member.Member;
import com.lineacademy.inventobackendspring.domain.report.Report;
import com.lineacademy.inventobackendspring.domain.user.User;
import com.lineacademy.inventobackendspring.domain.enums.ReportStatus;
import com.lineacademy.inventobackendspring.domain.enums.ReportType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class ReportResponseDTO {

    @Getter
    @Builder
    public static class ReportResponse {
        private Long id;
        private Long equipmentId;
        private Long reporterId;
        private ReportType type;
        private String title;
        private String content;
        private ReportStatus status;
        private LocalDateTime processedAt;
        private String result;
        private LocalDateTime createdAt;

        private ReporterSummary reporter;
        private EquipmentSummary equipment;

        public static ReportResponse from(Report report) {
            return ReportResponse.builder()
                    .id(report.getId())
                    .equipmentId(report.getEquipment() != null ? report.getEquipment().getId() : null)
                    .reporterId(report.getReporter().getId())
                    .type(report.getType())
                    .title(report.getTitle())
                    .content(report.getContent())
                    .status(report.getStatus())
                    .processedAt(report.getProcessedAt())
                    .result(report.getResult())
                    .createdAt(report.getCreatedAt())
                    .reporter(ReporterSummary.from(report.getReporter()))
                    .equipment(EquipmentSummary.from(report.getEquipment()))
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ReporterSummary {
        private Long id;
        private UserSummary user;
        private DepartmentSummary department;

        public static ReporterSummary from(Member member) {
            if (member == null) return null;
            return ReporterSummary.builder()
                    .id(member.getId())
                    .user(UserSummary.from(member.getUser()))
                    .department(DepartmentSummary.from(member.getDepartment()))
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

    @Getter
    @Builder
    public static class EquipmentSummary {
        private Long id;
        private String name;
        private String imageUrl;
        private String category;

        public static EquipmentSummary from(Equipment equipment) {
            if (equipment == null) return null;
            return EquipmentSummary.builder()
                    .id(equipment.getId())
                    .name(equipment.getName())
                    .imageUrl(equipment.getImageUrl())
                    .category(equipment.getCategory())
                    .build();
        }
    }
}