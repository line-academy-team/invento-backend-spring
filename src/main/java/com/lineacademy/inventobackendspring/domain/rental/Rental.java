package com.lineacademy.inventobackendspring.domain.rental;

import com.lineacademy.inventobackendspring.domain.common.BaseTimeEntity;
import com.lineacademy.inventobackendspring.domain.enums.RentalStatus;
import com.lineacademy.inventobackendspring.domain.equipment.Equipment;
import com.lineacademy.inventobackendspring.domain.equipmentunit.EquipmentUnit;
import com.lineacademy.inventobackendspring.domain.member.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "rentals")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rental extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column()
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentalStatus status = RentalStatus.REQUESTED;

    @CreatedDate
    @Column(name = "requested_at", nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejected_reason", length = 255)
    private String rejectedReason;

    @Column(name = "due_at")
    private LocalDateTime dueAt;

    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private Member approver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_unit_id")
    private EquipmentUnit equipmentUnit;

    @Builder
    private Rental(
            Equipment equipment,
            EquipmentUnit equipmentUnit,
            Member member,
            Integer quantity,
            String reason,
            LocalDateTime dueAt,
            RentalStatus status
    ) {
        this.equipment = equipment;
        this.equipmentUnit = equipmentUnit;
        this.member = member;
        this.reason = reason;
        this.dueAt = dueAt;
        if (quantity != null) this.quantity = quantity;
        if (status != null) this.status = status;
    }

    public void processRequest(RentalStatus status, Member approver, LocalDateTime approvedAt, String rejectedReason) {
        this.status = status;
        this.approver = approver;
        this.approvedAt = approvedAt;
        this.rejectedReason = rejectedReason;
    }

    public void markAsReturned() {
        this.status = RentalStatus.RETURNED;
        this.returnedAt = LocalDateTime.now();
    }

    public void updateQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void updateReason(String reason) {
        this.reason = reason;
    }

    public void updateDueAt(LocalDateTime dueAt) {
        this.dueAt = dueAt;
    }
}