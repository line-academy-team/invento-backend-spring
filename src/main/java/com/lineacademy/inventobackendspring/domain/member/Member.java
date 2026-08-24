package com.lineacademy.inventobackendspring.domain.member;

import com.lineacademy.inventobackendspring.domain.enums.MemberRole;
import com.lineacademy.inventobackendspring.domain.enums.MemberStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role = MemberRole.MEMBER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status = MemberStatus.PENDING;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejected_reason")
    private String rejectedReason;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Member approver;

    // TODO : Organization 관계 작성

    // TODO : User 관계 작성

    // TODO : Department 관계 작성

    @OneToMany(mappedBy = "approver")
    private List<Member> approvedMembers = new ArrayList<>();

    // TODO : Equipment 관계 작성

    // TODO : Rental 관계 작성

    // TODO : EquipmentStockRequest 관계 작성

    // TODO : EquipmentStockResponse 관계 작성

    // TODO : Report 관계 작성

    // TODO : Report 관계 작성

    @Builder
    public Member(
            Long id,
            MemberRole role,
            MemberStatus status,
            LocalDateTime approvedAt,
            String rejectedReason,
            LocalDateTime joinedAt,
            Member approver
    ) {
        this.id = id;
        this.role = role;
        this.status = status;
        this.approvedAt = approvedAt;
        this.rejectedReason = rejectedReason;
        this.joinedAt = joinedAt;
        this.approver = approver;
    }
}
