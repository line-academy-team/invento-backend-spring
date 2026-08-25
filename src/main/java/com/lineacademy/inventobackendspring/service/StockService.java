package com.lineacademy.inventobackendspring.service;

import com.lineacademy.inventobackendspring.domain.enums.EquipmentType;
import com.lineacademy.inventobackendspring.domain.enums.MemberStatus;
import com.lineacademy.inventobackendspring.domain.enums.RequestStatus;
import com.lineacademy.inventobackendspring.domain.equipment.Equipment;
import com.lineacademy.inventobackendspring.domain.equipmentstockrequest.EquipmentStockRequest;
import com.lineacademy.inventobackendspring.domain.member.Member;
import com.lineacademy.inventobackendspring.dto.stock.request.CreateStockRequest;
import com.lineacademy.inventobackendspring.dto.stock.request.UpdateStockRequest;
import com.lineacademy.inventobackendspring.repository.EquipmentRepository;
import com.lineacademy.inventobackendspring.repository.EquipmentStockRequestRepository;
import com.lineacademy.inventobackendspring.repository.MemberRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {
    private final EquipmentStockRequestRepository stockRepository;
    private final EquipmentRepository equipmentRepository;
    private final MemberRepository memberRepository;

    private Member getMemberByUserId(Long userId) {
        return memberRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("MEMBER_NOT_FOUND"));
    }

    @Transactional
    public EquipmentStockRequest createStock(Long userId, @Valid CreateStockRequest request) {
        Member member = getMemberByUserId(userId);

        Equipment equipment = equipmentRepository.findById(request.getEquipmentId())
                .orElseThrow(() -> new RuntimeException("EQUIPMENT_NOT_FOUND"));

        if (!equipment.getOrganization().getId().equals(member.getOrganization().getId())) {
            throw new RuntimeException("EQUIPMENT_NOT_FOUND");
        }

        if (equipment.getType() != EquipmentType.CONSUMABLE && request.getQuantity() != 1) {
            throw new RuntimeException("INDIVIDUAL_EQUIPMENT_QUANTITY_MUST_BE_ONE");
        }

        EquipmentStockRequest stockRequest = EquipmentStockRequest.builder()
                .requester(member)
                .equipment(equipment)
                .quantity(request.getQuantity())
                .reason(request.getReason())
                .status(RequestStatus.PENDING)
                .build();

        return stockRepository.save(stockRequest);
    }

    @Transactional(readOnly = true)
    public List<EquipmentStockRequest> getStockList(Long userId, Long ozId) {
        if (ozId != null) {
            boolean isMember = memberRepository.existsByUserIdAndOrganizationIdAndStatus(userId, ozId, MemberStatus.APPROVED);
            if (!isMember) {
                throw new RuntimeException("MEMBER_NOT_FOUND");
            }
            return stockRepository.findAllByRequesterOrganizationIdOrderByCreatedAtDesc(ozId);
        } else {
            Member member = getMemberByUserId(userId);
            return stockRepository.findAllByRequesterOrganizationIdOrderByCreatedAtDesc(member.getId());
        }
    }

    @Transactional
    public EquipmentStockRequest updateStockRequest(Long userId, Long stockId, @Valid UpdateStockRequest request) {
        Member member = getMemberByUserId(userId);

        EquipmentStockRequest stock = stockRepository.findByIdAndRequesterId(stockId, member.getId())
                .orElseThrow(() -> new RuntimeException("STOCK_NOT_FOUND"));

        if (stock.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("CANNOT_UPDATE_APPROVED_STOCK");
        }

        Equipment equipment = stock.getEquipment();
        if (equipment.getType() != EquipmentType.CONSUMABLE &&
                request.getQuantity() != null && request.getQuantity() != 1
        ) {
            throw new RuntimeException("INDIVIDUAL_EQUIPMENT_QUANTITY_MUST_BE_ONE");
        }

        if (request.getQuantity() != null) {
            stock.updateQuantity(request.getQuantity());
        }
        if (request.getReason() != null) {
            stock.updateReason(request.getReason());
        }

        return stock;
    }

    @Transactional
    public void deleteStockRequest(Long userId, Long stockId) {
        Member member = getMemberByUserId(userId);

        EquipmentStockRequest stock = stockRepository.findByIdAndRequesterId(stockId, member.getId())
                .orElseThrow(() -> new RuntimeException("STOCK_NOT_FOUND"));

        if (stock.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("CANNOT_CANCEL_APPROVED_STOCK");
        }

        stockRepository.delete(stock);
    }
}
