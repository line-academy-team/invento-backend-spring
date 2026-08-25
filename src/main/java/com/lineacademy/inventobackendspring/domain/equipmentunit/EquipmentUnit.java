package com.lineacademy.inventobackendspring.domain.equipmentunit;

import com.lineacademy.inventobackendspring.domain.common.BaseTimeEntity;
import com.lineacademy.inventobackendspring.domain.enums.EquipmentStatus;
import com.lineacademy.inventobackendspring.domain.equipment.Equipment;
import com.lineacademy.inventobackendspring.domain.rental.Rental;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "equipment_units")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EquipmentUnit extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "asset_number", nullable = false, unique = true)
    private String assetNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentStatus status = EquipmentStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    // TODO : Rental 관계 작성
    @OneToMany(mappedBy = "equipmentUnit")
    private List<Rental> rentals = new ArrayList<>();

    @Builder
    private EquipmentUnit(
            String assetNumber,
            EquipmentStatus status,
            Equipment equipment
    ) {
        this.assetNumber = assetNumber;
        this.equipment = equipment;
        if (status != null) this.status = status;
    }

    public void updateAssetNumber(String assetNumber) {
        this.assetNumber = assetNumber;
    }

    public void updateStatus(EquipmentStatus status) {
        this.status = status;
    }
}
