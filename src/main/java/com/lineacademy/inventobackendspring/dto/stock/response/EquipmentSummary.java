package com.lineacademy.inventobackendspring.dto.stock.response;

import com.lineacademy.inventobackendspring.domain.equipment.Equipment;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EquipmentSummary {
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
