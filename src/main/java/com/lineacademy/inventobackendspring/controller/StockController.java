package com.lineacademy.inventobackendspring.controller;

import com.lineacademy.inventobackendspring.domain.equipmentstockrequest.EquipmentStockRequest;
import com.lineacademy.inventobackendspring.dto.stock.request.CreateStockRequest;
import com.lineacademy.inventobackendspring.dto.stock.request.UpdateStockRequest;
import com.lineacademy.inventobackendspring.dto.stock.response.StockListResponse;
import com.lineacademy.inventobackendspring.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/request")
    public ResponseEntity<Map<String, Object>> createStockRequest(
            @AuthenticationPrincipal Long currentUserId,
            @Valid @RequestBody CreateStockRequest request
    ) {
        try {
            EquipmentStockRequest stock = stockService.createStock(currentUserId, request);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "재고 수량 요청이 완료되었습니다. 관리자 승인을 대기해주세요.",
                    "data", StockListResponse.from(stock)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("EQUIPMENT_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "장비를 찾을 수 없습니다."));
            }
            if (e.getMessage().equals("INDIVIDUAL_EQUIPMENT_QUANTITY_MUST_BE_ONE")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "소모품이 아닌 장비는 1개만 요청할 수 있습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "재고 수량 요청 중 서버 에러가 발생했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getStockList(
            @AuthenticationPrincipal Long currentUserId,
            @RequestParam(required = false) Long ozId
    ) {
        try {
            List<EquipmentStockRequest> stocks = stockService.getStockList(currentUserId, ozId);

            List<StockListResponse> responseData = stocks.stream()
                    .map(StockListResponse::from)
                    .toList();

            return ResponseEntity.ok(Map.of(
                    "message", "내 재고 수량 요청 목록을 불러왔습니다.",
                    "data", responseData
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("MEMBER_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "message", "소속된 단체 멤버 정보를 찾을 수 없습니다."
                ));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "내 재고 수량 요청 목록을 불러오는 중 서버 에러가 발생했습니다."
            ));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{stockId}")
    public ResponseEntity<Map<String, Object>> updateStockRequest(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long stockId,
            @Valid @RequestBody UpdateStockRequest request
            ) {
        try {
            EquipmentStockRequest updatedStock = stockService.updateStockRequest(currentUserId, stockId, request);
            return ResponseEntity.ok(Map.of(
                    "message", "재고 요청이 수정이 완료되었습니다.",
                    "data", StockListResponse.from(updatedStock)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("STOCK_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "message", "재고 요청 내역을 찾을 수 없거나 권한이 없습니다."
                ));
            }
            if (e.getMessage().equals("CANNOT_UPDATE_APPROVED_STOCK")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                        "message", "승인 대기 중인 요청만 수정할 수 있습니다."
                ));
            }
            if (e.getMessage().equals("INDIVIDUAL_EQUIPMENT_QUANTITY_MUST_BE_ONE")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                        "message", "소모품이 아닌 장비는 한 번에 1개만 요청할 수 있습니다."
                ));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "재고 요청 수정 중 서버 에러가 발생했습니다."
            ));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{stockId}")
    public ResponseEntity<Map<String, Object>> deleteStockRequest(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long stockId
    ) {
        try {
            stockService.deleteStockRequest(currentUserId, stockId);
            return ResponseEntity.ok(Map.of("message", "재고 요청 취소가 완료되었습니다."));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("STOCK_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "message", "재고 요청 내역을 찾을 수 없거나 권한이 없습니다"
                ));
            }
            if (e.getMessage().equals("CANNOT_CANCEL_APPROVED_STOCK")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                        "message", "승인 대기 증인 요청만 취소할 수 있습니다."
                ));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "재고 요청 취소 중 서버 에러가 발생했습니다."
            ));
        }
    }
}
