package com.lineacademy.inventobackendspring.dto.stock.response;

import com.lineacademy.inventobackendspring.domain.enums.RequestStatus;
import com.lineacademy.inventobackendspring.domain.equipmentstockrequest.EquipmentStockRequest;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StockListResponse {
    private Long id;
    private Integer quantity;
    private String reason;
    private RequestStatus status;
    private LocalDateTime createdAt;
    private EquipmentSummary equipment;

    public static StockListResponse from(EquipmentStockRequest stock) {
        return StockListResponse.builder()
                .id(stock.getId())
                .quantity(stock.getQuantity())
                .reason(stock.getReason())
                .createdAt(stock.getCreatedAt())
                .equipment(EquipmentSummary.from(stock.getEquipment()))
                .build();
    }
}
