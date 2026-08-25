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

    public void updateName(String name) {
        this.name = name;
    }

    public void updateType(EquipmentType type) {
        this.type = type;
    }

    public void updateTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public void updateCategory(String category) {
        this.category = category;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updateDepartment(Department department) {
        this.department = department;
    }

    public void increaseAvailableQuantity(Integer quantity) {
        if (quantity == null || quantity < 0) return;

        if (this.available_quantity == null) {
            this.available_quantity = 0;
        }

        this.available_quantity += quantity;

        // 가용 수량이 전체 수량을 초과하지 않도록 보정 (선택 사항)
        if (this.available_quantity > this.totalQuantity) {
            this.available_quantity = this.totalQuantity;
        }
    }

    public void decreaseAvailableQuantity(Integer quantity) {
        if (quantity == null || quantity < 0) return;

        if (this.available_quantity == null) {
            this.available_quantity = 0;
        }

        if (this.available_quantity < quantity) {
            throw new RuntimeException("AVAILABLE_QUANTITY_NOT_ENOUGH");
        }

        this.available_quantity -= quantity;
    }
}


