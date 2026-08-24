package com.lineacademy.inventobackendspring.domain.equipment;

import com.lineacademy.inventobackendspring.domain.common.BaseTimeEntity;
import com.lineacademy.inventobackendspring.domain.department.Department;
import com.lineacademy.inventobackendspring.domain.enums.EquipmentType;
import com.lineacademy.inventobackendspring.domain.enums.EquipmentsStatus;
import com.lineacademy.inventobackendspring.domain.equipmentstockrequest.EquipmentStockRequest;
import com.lineacademy.inventobackendspring.domain.equipmentunit.EquipmentUnit;
import com.lineacademy.inventobackendspring.domain.member.Member;
import com.lineacademy.inventobackendspring.domain.organization.Organization;
import com.lineacademy.inventobackendspring.domain.rental.Rental;
import com.lineacademy.inventobackendspring.domain.report.Report;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "equipment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Equipment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column()
    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentType type;

    @Column(name = "total_quantity", nullable = false)
    private Integer totalQuantity = 0;

    @Column(name = "available_quantity", nullable = false)
    private Integer available_quantity = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentsStatus status = EquipmentsStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private Member creator;

    @OneToMany(mappedBy = "equipment")
    private List<EquipmentUnit> units = new ArrayList<>();

    // TODO : Rental 관계 작성
    @OneToMany(mappedBy = "equipment")
    private List<Rental> rentals = new ArrayList<>();

    // TODO : EquipmentStockRequest 관계 작성
    @OneToMany(mappedBy = "equipment")
    private List<EquipmentStockRequest> stockRequests = new ArrayList<>();

    // TODO : Report 관계 작성
    @OneToMany(mappedBy = "equipment")
    private List<Report> reports = new ArrayList<>();

    @Builder
    private Equipment (
            String name,
            String category,
            String description,
            String imageUrl,
            EquipmentType type,
            Integer totalQuantity,
            Integer available_quantity,
            EquipmentsStatus status,
            Organization organization,
            Department department,
            Member creator
    ) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.imageUrl = imageUrl;
        this.type = type;
        this.totalQuantity = totalQuantity;
        this.available_quantity = available_quantity;
        this.status = status;
        this.organization = organization;
        this.department = department;
        this.creator = creator;
    }
}
