package com.lineacademy.inventobackendspring.domain.member;

import com.lineacademy.inventobackendspring.domain.common.BaseTimeEntity;
import com.lineacademy.inventobackendspring.domain.department.Department;
import com.lineacademy.inventobackendspring.domain.enums.MemberRole;
import com.lineacademy.inventobackendspring.domain.enums.MemberStatus;
import com.lineacademy.inventobackendspring.domain.equipment.Equipment;
import com.lineacademy.inventobackendspring.domain.equipmentstockrequest.EquipmentStockRequest;
import com.lineacademy.inventobackendspring.domain.organization.Organization;
import com.lineacademy.inventobackendspring.domain.rental.Rental;
import com.lineacademy.inventobackendspring.domain.report.Report;
import com.lineacademy.inventobackendspring.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
     private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToMany(mappedBy = "approver")
    private List<Member> approvedMembers = new ArrayList<>();

    @OneToMany(mappedBy = "creator")
    private List<Equipment> equipments = new ArrayList<>();

    // TODO : Rental 관계 작성
    @OneToMany(mappedBy = "approver")
    private List<Rental> approvedRentals = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Rental> rentals = new ArrayList<>();

    // TODO : EquipmentStockRequest 관계 작성
    @OneToMany(mappedBy = "requester")
    private List<EquipmentStockRequest> requestedStock =new ArrayList<>();

    // TODO : EquipmentStockResponse 관계 작성
    @OneToMany(mappedBy = "processor")
    private List<EquipmentStockRequest> processedStockRequests = new ArrayList<>();

    // TODO : Report 관계 작성
    @OneToMany(mappedBy = "reporter")
    private List<Report> reports = new ArrayList<>();

    // TODO : Report 관계 작성
    @OneToMany(mappedBy = "processor")
    private List<Report> processedReports = new ArrayList<>();

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
