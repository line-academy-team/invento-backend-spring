package com.lineacademy.inventobackendspring.domain.equipmentstockrequest;

import com.lineacademy.inventobackendspring.domain.common.BaseTimeEntity;
import com.lineacademy.inventobackendspring.domain.enums.RequestStatus;
import com.lineacademy.inventobackendspring.domain.equipment.Equipment;
import com.lineacademy.inventobackendspring.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.coyote.Request;


import java.time.LocalDateTime;

@Entity
@Table(name = "equipment_stock_requests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EquipmentStockRequest extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "rejected_reason", length = 255)
    private String rejectedReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private Member requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by")
    private Member processor;

    @Builder
    private EquipmentStockRequest(
            Equipment equipment,
            Member requester,
            Integer quantity,
            String reason,
            RequestStatus status
    ) {
        this.equipment = equipment;
        this.requester = requester;
        this.quantity = quantity;
        this.reason = reason;
        if (status != null) this.status = status;
    }

    public void processRequest(
            Member processor,
            RequestStatus status,
            String rejectedReason
    ) {
        this.processor = processor;
        this.status = status;
        this.processedAt = LocalDateTime.now();
        this.rejectedReason = rejectedReason;
    }
}