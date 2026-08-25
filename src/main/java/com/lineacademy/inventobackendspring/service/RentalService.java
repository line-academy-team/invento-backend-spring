package com.lineacademy.inventobackendspring.service;

import com.lineacademy.inventobackendspring.domain.equipment.Equipment;
import com.lineacademy.inventobackendspring.domain.equipmentunit.EquipmentUnit;
import com.lineacademy.inventobackendspring.domain.member.Member;
import com.lineacademy.inventobackendspring.domain.rental.Rental;
import com.lineacademy.inventobackendspring.domain.enums.EquipmentType;
import com.lineacademy.inventobackendspring.domain.enums.MemberRole;
import com.lineacademy.inventobackendspring.domain.enums.MemberStatus;
import com.lineacademy.inventobackendspring.domain.enums.RentalStatus;
import com.lineacademy.inventobackendspring.dto.rental.request.CreateRentalRequest;
import com.lineacademy.inventobackendspring.dto.rental.request.ProcessRentalRequest;
import com.lineacademy.inventobackendspring.dto.rental.request.UpdateRentalRequest;
import com.lineacademy.inventobackendspring.repository.EquipmentRepository;
import com.lineacademy.inventobackendspring.repository.EquipmentUnitRepository;
import com.lineacademy.inventobackendspring.repository.MemberRepository;
import com.lineacademy.inventobackendspring.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final MemberRepository memberRepository;
    private final EquipmentRepository equipmentRepository;
    private final EquipmentUnitRepository equipmentUnitRepository;

    private Member getMemberByUserId(Long userId) {
        return memberRepository.findFirstByUserIdAndStatus(userId, MemberStatus.APPROVED)
                .orElseThrow(() -> new RuntimeException("MEMBER_NOT_FOUND"));
    }

    private Member getManagerMemberByUserId(Long userId, Long ozId) {
        return memberRepository.findFirstByUserIdAndOrganizationIdAndStatusAndRoleIn(
                        userId, ozId, MemberStatus.APPROVED, Arrays.asList(MemberRole.OWNER, MemberRole.MANAGER))
                .orElseThrow(() -> new RuntimeException("MANAGER_PERMISSION_REQUIRED"));
    }

    @Transactional(readOnly = true)
    public List<Rental> getMyRentalList(Long userId) {
        Member member = getMemberByUserId(userId);
        return rentalRepository.findByMemberIdOrderByCreatedAtDesc(member.getId());
    }

    @Transactional(readOnly = true)
    public Rental getMyRentalById(Long userId, Long rentalId) {
        Member member = getMemberByUserId(userId);
        return rentalRepository.findByIdAndMemberId(rentalId, member.getId())
                .orElseThrow(() -> new RuntimeException("RENTAL_NOT_FOUND"));
    }

    @Transactional(readOnly = true)
    public List<Rental> getOrgRentalList(Long userId, Long ozId) {
        getManagerMemberByUserId(userId, ozId);
        return rentalRepository.findByEquipmentOrganizationIdOrderByCreatedAtDesc(ozId);
    }

    @Transactional(readOnly = true)
    public Rental getOrgRentalById(Long userId, Long ozId, Long rentalId) {
        getManagerMemberByUserId(userId, ozId);
        return rentalRepository.findByIdAndEquipmentOrganizationId(rentalId, ozId)
                .orElseThrow(() -> new RuntimeException("RENTAL_NOT_FOUND"));
    }

    @Transactional
    public Rental processRental(Long userId, Long ozId, Long rentalId, ProcessRentalRequest request) {
        Member manager = getManagerMemberByUserId(userId, ozId);

        Rental rental = rentalRepository.findByIdAndEquipmentOrganizationId(rentalId, ozId)
                .orElseThrow(() -> new RuntimeException("RENTAL_NOT_FOUND"));

        if (rental.getStatus() != RentalStatus.REQUESTED) {
            throw new RuntimeException("RENTAL_ALREADY_PROCESSED");
        }

        if (request.getStatus() == RentalStatus.REJECTED) {
            rental.getEquipment().increaseAvailableQuantity(rental.getQuantity());
        }

        rental.processRequest(
                request.getStatus(),
                request.getStatus() == RentalStatus.BORROWED ? manager : null,
                request.getStatus() == RentalStatus.BORROWED ? LocalDateTime.now() : null,
                request.getStatus() == RentalStatus.REJECTED ? request.getRejectedReason() : null
        );

        return rental;
    }

    @Transactional
    public Rental createRental(Long userId, CreateRentalRequest request) {
        Member member = getMemberByUserId(userId);

        Equipment equipment = equipmentRepository.findById(request.getEquipmentId())
                .orElseThrow(() -> new RuntimeException("EQUIPMENT_NOT_FOUND"));

        int reqQuantity = request.getQuantity() != null ? request.getQuantity() : 1;

        if (equipment.getType() != EquipmentType.CONSUMABLE && reqQuantity != 1) {
            throw new RuntimeException("INDIVIDUAL_EQUIPMENT_QUANTITY_MUST_BE_ONE");
        }
        if (equipment.getAvailableQuantity() < reqQuantity) {
            throw new RuntimeException("AVAILABLE_QUANTITY_NOT_ENOUGH");
        }

        equipment.decreaseAvailableQuantity(reqQuantity);

        EquipmentUnit unit = null;
        if (request.getEquipmentUnitId() != null) {
            unit = equipmentUnitRepository.findById(request.getEquipmentUnitId()).orElse(null);
        }

        Rental rental = Rental.builder()
                .member(member)
                .equipment(equipment)
                .equipmentUnit(unit)
                .quantity(reqQuantity)
                .reason(request.getReason())
                .dueAt(request.getDueAt())
                .status(RentalStatus.REQUESTED)
                .build();

        return rentalRepository.save(rental);
    }

    @Transactional
    public void returnRental(Long userId, Long rentalId) {
        Member member = getMemberByUserId(userId);

        Rental rental = rentalRepository.findByIdAndMemberId(rentalId, member.getId())
                .orElseThrow(() -> new RuntimeException("RENTAL_NOT_FOUND"));

        if (rental.getStatus() != RentalStatus.BORROWED) {
            throw new RuntimeException("INVALID_RENTAL_STATUS");
        }

        rental.getEquipment().increaseAvailableQuantity(rental.getQuantity());

        rental.markAsReturned();
    }

    @Transactional
    public Rental updateRental(Long userId, Long rentalId, UpdateRentalRequest request) {
        Member member = getMemberByUserId(userId);

        Rental rental = rentalRepository.findByIdAndMemberId(rentalId, member.getId())
                .orElseThrow(() -> new RuntimeException("RENTAL_NOT_FOUND"));

        if (rental.getStatus() != RentalStatus.REQUESTED) {
            throw new RuntimeException("CANNOT_UPDATE_APPROVED_RENTAL");
        }

        Equipment equipment = rental.getEquipment();
        if (equipment.getType() != EquipmentType.CONSUMABLE && request.getQuantity() != null && request.getQuantity() != 1) {
            throw new RuntimeException("INDIVIDUAL_EQUIPMENT_QUANTITY_MUST_BE_ONE");
        }

        if (request.getQuantity() != null) {
            rental.updateQuantity(request.getQuantity());
        }
        if (request.getReason() != null) rental.updateReason(request.getReason());
        if (request.getDueAt() != null) rental.updateDueAt(request.getDueAt());

        return rental;
    }

    @Transactional
    public void deleteRental(Long userId, Long rentalId) {
        Member member = getMemberByUserId(userId);

        Rental rental = rentalRepository.findByIdAndMemberId(rentalId, member.getId())
                .orElseThrow(() -> new RuntimeException("RENTAL_NOT_FOUND"));

        if (rental.getStatus() != RentalStatus.REQUESTED) {
            throw new RuntimeException("CANNOT_CANCEL_APPROVED_RENTAL");
        }

        rental.getEquipment().increaseAvailableQuantity(rental.getQuantity());

        rentalRepository.delete(rental);
    }
}
