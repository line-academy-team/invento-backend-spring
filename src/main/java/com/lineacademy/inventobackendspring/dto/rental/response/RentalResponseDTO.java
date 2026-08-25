package com.lineacademy.inventobackendspring.dto.rental.response;

import com.lineacademy.inventobackendspring.domain.equipment.Equipment;
import com.lineacademy.inventobackendspring.domain.equipmentunit.EquipmentUnit;
import com.lineacademy.inventobackendspring.domain.member.Member;
import com.lineacademy.inventobackendspring.domain.rental.Rental;
import com.lineacademy.inventobackendspring.domain.enums.RentalStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class RentalResponseDTO {

    @Getter
    @Builder
    public static class MyRentalResponse {
        private Long id;
        private Integer quantity;
        private String reason;
        private RentalStatus status;
        private LocalDateTime dueAt;
        private LocalDateTime createdAt;
        private EquipmentSummary equipment;
        private UnitSummary equipmentUnit;

        public static MyRentalResponse from(Rental rental) {
            return MyRentalResponse.builder()
                    .id(rental.getId())
                    .quantity(rental.getQuantity())
                    .reason(rental.getReason())
                    .status(rental.getStatus())
                    .dueAt(rental.getDueAt())
                    .createdAt(rental.getCreatedAt())
                    .equipment(EquipmentSummary.from(rental.getEquipment()))
                    .equipmentUnit(UnitSummary.from(rental.getEquipmentUnit()))
                    .build();
        }
    }

    @Getter
    @Builder
    public static class OrgRentalResponse {
        private Long id;
        private Integer quantity;
        private String reason;
        private RentalStatus status;
        private String rejectedReason;
        private LocalDateTime requestedAt;
        private LocalDateTime dueAt;
        private LocalDateTime returnedAt;

        private EquipmentSummary equipment;
        private UnitSummary equipmentUnit;
        private MemberSummary member;

        public static OrgRentalResponse from(Rental rental) {
            return OrgRentalResponse.builder()
                    .id(rental.getId())
                    .quantity(rental.getQuantity())
                    .reason(rental.getReason())
                    .status(rental.getStatus())
                    .rejectedReason(rental.getRejectedReason())
                    .requestedAt(rental.getRequestedAt())
                    .dueAt(rental.getDueAt())
                    .returnedAt(rental.getReturnedAt())
                    .equipment(EquipmentSummary.from(rental.getEquipment()))
                    .equipmentUnit(UnitSummary.from(rental.getEquipmentUnit()))
                    .member(MemberSummary.from(rental.getMember()))
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
        private String status;

        public static EquipmentSummary from(Equipment equipment) {
            if (equipment == null) return null;
            return EquipmentSummary.builder()
                    .id(equipment.getId())
                    .name(equipment.getName())
                    .imageUrl(equipment.getImageUrl())
                    .category(equipment.getCategory())
                    .status(equipment.getStatus().name())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class UnitSummary {
        private Long id;
        private String assetNumber;

        public static UnitSummary from(EquipmentUnit unit) {
            if (unit == null) return null;
            return UnitSummary.builder()
                    .id(unit.getId())
                    .assetNumber(unit.getAssetNumber())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class MemberSummary {
        private Long id;
        private String name;
        private String email;
        private String departmentName;

        public static MemberSummary from(Member member) {
            if (member == null) return null;
            return MemberSummary.builder()
                    .id(member.getId())
                    .name(member.getUser().getName())
                    .email(member.getUser().getEmail())
                    .departmentName(member.getDepartment() != null ? member.getDepartment().getName() : null)
                    .build();
        }
    }
}
