package com.lineacademy.inventobackendspring.domain.rental;

import com.lineacademy.inventobackendspring.domain.enums.RentalStatus;
import com.lineacardemy.inventobackendspring.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
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
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_unit_id")
    private EquipmentUnit equipmentUnit;


}
