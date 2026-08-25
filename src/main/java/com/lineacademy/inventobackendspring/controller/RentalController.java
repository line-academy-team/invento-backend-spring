package com.lineacademy.inventobackendspring.controller;

import com.lineacademy.inventobackendspring.domain.rental.Rental;
import com.lineacademy.inventobackendspring.domain.enums.RentalStatus;
import com.lineacademy.inventobackendspring.dto.rental.request.UpdateRentalRequest;
import com.lineacademy.inventobackendspring.dto.rental.request.ProcessRentalRequest;
import com.lineacademy.inventobackendspring.dto.rental.request.CreateRentalRequest;
import com.lineacademy.inventobackendspring.dto.rental.response.RentalResponseDTO;
import com.lineacademy.inventobackendspring.service.RentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rental")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/request")
    public ResponseEntity<Map<String, Object>> createRentalRequest(
            @AuthenticationPrincipal Long currentUserId,
            @Valid @RequestBody CreateRentalRequest request
    ) {
        try {
            Rental newRental = rentalService.createRental(currentUserId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "대여 신청이 완료되었습니다. 관리자 승인을 대기해주세요.",
                    "data", RentalResponseDTO.MyRentalResponse.from(newRental)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("INDIVIDUAL_EQUIPMENT_QUANTITY_MUST_BE_ONE")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                        "message", "소모품이 아닌 장비는 한 번에 1개만 대여할 수 있습니다."
                ));
            }
            if (e.getMessage().equals("AVAILABLE_QUANTITY_NOT_ENOUGH")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                        "message", "대여 가능한 재고 수량이 부족합니다."
                ));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "대여 신청 중 서버 에러가 발생하였습니다."
            ));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMyRentalList(@AuthenticationPrincipal Long currentUserId) {
        try {
            List<Rental> rentals = rentalService.getMyRentalList(currentUserId);
            return ResponseEntity.ok(Map.of(
                    "message", "내 대여 신청 목록을 불러왔습니다.",
                    "data", rentals.stream().map(RentalResponseDTO.MyRentalResponse::from).collect(Collectors.toList())
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("MEMBER_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "소속된 단체 멤버 정보를 찾을 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "내 대여 신청 목록을 불러오는 중 서버 에러가 발생했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/{rentalId}")
    public ResponseEntity<Map<String, Object>> getMyRentalById(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long rentalId
    ) {
        try {
            Rental rental = rentalService.getMyRentalById(currentUserId, rentalId);
            return ResponseEntity.ok(Map.of(
                    "message", "대여 상세 내역을 불러왔습니다.",
                    "data", RentalResponseDTO.MyRentalResponse.from(rental)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("RENTAL_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "대여 내역을 찾을 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "대여 상세 조회 중 서버 에러가 발생했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{ozId}")
    public ResponseEntity<Map<String, Object>> getOrgRentalList(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long ozId
    ) {
        try {
            List<Rental> rentals = rentalService.getOrgRentalList(currentUserId, ozId);
            return ResponseEntity.ok(Map.of(
                    "message", "조직 대여 신청 목록을 불러왔습니다.",
                    "data", rentals.stream().map(RentalResponseDTO.OrgRentalResponse::from).collect(Collectors.toList())
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("MANAGER_PERMISSION_REQUIRED")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "대여 관리 권한이 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "조직 대여 신청 목록을 불러오는 중 서버 에러가 발생했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{ozId}/{rentalId}")
    public ResponseEntity<Map<String, Object>> getOrgRentalById(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long ozId,
            @PathVariable Long rentalId
    ) {
        try {
            Rental rental = rentalService.getOrgRentalById(currentUserId, ozId, rentalId);
            return ResponseEntity.ok(Map.of(
                    "message", "대여 요청 상세를 불러왔습니다.",
                    "data", RentalResponseDTO.OrgRentalResponse.from(rental)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("MANAGER_PERMISSION_REQUIRED")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "대여 관리 권한이 없습니다."));
            }
            if (e.getMessage().equals("RENTAL_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "대여 요청을 찾을 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "대여 요청 상세 조회 중 서버 에러가 발생했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{ozId}/{rentalId}/process")
    public ResponseEntity<Map<String, Object>> processRental(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long ozId,
            @PathVariable Long rentalId,
            @Valid @RequestBody ProcessRentalRequest request
    ) {
        try {
            Rental rental = rentalService.processRental(currentUserId, ozId, rentalId, request);
            String message = request.getStatus() == RentalStatus.BORROWED ? "대여 요청을 승인했습니다." : "대여 요청을 반려했습니다.";
            return ResponseEntity.ok(Map.of(
                    "message", message,
                    "data", RentalResponseDTO.OrgRentalResponse.from(rental)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("MANAGER_PERMISSION_REQUIRED")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "message", "대여 관리 권한이 없습니다."
                ));
            }
            if (e.getMessage().equals("RENTAL_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "message", "대여 요청을 찾을 수 없습니다."
                ));
            }
            if (e.getMessage().equals("RENTAL_ALREADY_PROCESSED")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                        "message", "이미 처리된 대여 요청입니다."
                ));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "대여 요청 처리 중 서버 에러가 발생했습니다."
            ));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/return/{rentalId}")
    public ResponseEntity<Map<String, Object>> returnRental(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long rentalId
    ) {
        try {
            rentalService.returnRental(currentUserId, rentalId);
            return ResponseEntity.ok(Map.of("message", "반납이 완료되었습니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "비품 반납 중 서버 에러가 발생하였습니다."
            ));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{rentalId}")
    public ResponseEntity<Map<String, Object>> updateRental(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long rentalId,
            @Valid @RequestBody UpdateRentalRequest request
    ) {
        try {
            Rental updatedRental = rentalService.updateRental(currentUserId, rentalId, request);
            return ResponseEntity.ok(Map.of(
                    "message", "대여 신청이 수정되었습니다.",
                    "data", RentalResponseDTO.MyRentalResponse.from(updatedRental)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("RENTAL_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "message", "대여 내역을 찾을 수 없거나 권한이 없습니다."
                ));
            }
            if (e.getMessage().equals("CANNOT_UPDATE_APPROVED_RENTAL")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                        "message", "승인 대기 중인 대여 신청만 수정할 수 있습니다."
                ));
            }
            if (e.getMessage().equals("INDIVIDUAL_EQUIPMENT_QUANTITY_MUST_BE_ONE")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                        "message", "소모품이 아닌 장비는 한 번에 1개만 대여할 수 있습니다."
                ));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "대여 신청 수정 중 서버 에러가 발생했습니다."
            ));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{rentalId}")
    public ResponseEntity<Map<String, Object>> deleteRental(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long rentalId
    ) {
        try {
            rentalService.deleteRental(currentUserId, rentalId);
            return ResponseEntity.ok(Map.of("message", "대여 신청 취소가 완료되었습니다."));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("RENTAL_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "message", "대여 내역을 찾을 수 없거나 권한이 없습니다."
                ));
            }
            if (e.getMessage().equals("CANNOT_CANCEL_APPROVED_RENTAL")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                        "message", "승인 대기 중인 대여 신청만 취소할 수 있습니다."
                ));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "대여 신청 취소 중 서버 에러가 발생했습니다."
            ));
        }
    }
}
