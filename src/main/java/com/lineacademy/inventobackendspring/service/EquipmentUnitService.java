package com.lineacademy.inventobackendspring.service;

import com.lineacademy.inventobackendspring.domain.equipment.Equipment;
import com.lineacademy.inventobackendspring.domain.equipmentunit.EquipmentUnit;
import com.lineacademy.inventobackendspring.domain.member.Member;
import com.lineacademy.inventobackendspring.domain.enums.EquipmentsStatus;
import com.lineacademy.inventobackendspring.domain.enums.MemberRole;
import com.lineacademy.inventobackendspring.dto.equipmentunit.request.CreateEquipmentUnitRequest;
import com.lineacademy.inventobackendspring.dto.equipmentunit.request.UpdateEquipmentUnitRequest;
import com.lineacademy.inventobackendspring.repository.EquipmentRepository;
import com.lineacademy.inventobackendspring.repository.EquipmentUnitRepository;
import com.lineacardemy.inventobackendspring.reposityory.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentUnitService {
    private final EquipmentUnitRepository equipmentUnitRepository;
    private final EquipmentRepository equipmentRepository;
    private final MemberRepository memberRepository;

    private Member getMemberByUserId(Long userId) {
        return memberRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("MEMBER_NOT_FOUND"));
    }

    @Transactional(readOnly = true)
    public List<EquipmentUnit> getUnitsByEquipmentId(Long userId, Long equipmentId) {
        Member member = getMemberByUserId(userId);

        Long targetDepartmentId = member.getRole() == MemberRole.OWNER ? null :
                (member.getDepartment() != null ? member.getDepartment().getId() : null);

        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("UNIT_NOT_FOUND"));

        boolean isNotInOrg = !equipment.getOrganization().getId().equals(member.getOrganization().getId());
        boolean isNotInDept = targetDepartmentId != null &&
                (equipment.getDepartment() == null || !equipment.getDepartment().getId().equals(targetDepartmentId));

        if (isNotInOrg || isNotInDept) {
            throw new RuntimeException("EQUIPMENTUNIT_NOT_IN_ORGANIZATION_OR_DEPARTMENT");
        }

        return equipmentUnitRepository.findByEquipmentIdOrderByCreatedAtDesc(equipmentId);
    }

    @Transactional
    public EquipmentUnit createEquipmentUnit(Long userId, CreateEquipmentUnitRequest request) {
        Member member = getMemberByUserId(userId);

        if (member.getRole() == MemberRole.MEMBER) {
            throw new RuntimeException("CANNOT_CREATE_EQUIPMENTUNIT_MEMBER");
        }

        Equipment equipment = equipmentRepository.findById(request.getEquipmentId())
                .orElseThrow(() -> new RuntimeException("UNIT_NOT_FOUND"));

        EquipmentUnit unit = EquipmentUnit.builder()
                .equipment(equipment)
                .assetNumber(request.getAssetNumber())
                .status(request.getStatus() != null ? request.getStatus() : EquipmentsStatus.AVAILABLE)
                .build();

        return equipmentUnitRepository.save(unit);
    }

    @Transactional
    public EquipmentUnit updateEquipmentUnit(Long userId, Long unitId, UpdateEquipmentUnitRequest request) {
        Member member = getMemberByUserId(userId);

        if (member.getRole() == MemberRole.MEMBER) {
            throw new RuntimeException("CANNOT_UPDATE_EQUIPMENTUNIT_MEMBER");
        }

        EquipmentUnit unit = equipmentUnitRepository.findById(unitId)
                .orElseThrow(() -> new RuntimeException("UNIT_NOT_FOUND"));

        if (request.getAssetNumber() != null) {
            unit.updateAssetNumber(request.getAssetNumber());
        }
        if (request.getStatus() != null) {
            unit.updateStatus(request.getStatus());
        }

        return unit;
    }

    @Transactional
    public void deleteEquipmentUnit(Long userId, Long unitId) {
        Member member = getMemberByUserId(userId);

        if (member.getRole() == MemberRole.MEMBER) {
            throw new RuntimeException("CANNOT_DELETE_EQUIPMENTUNIT_MEMBER");
        }

        EquipmentUnit unit = equipmentUnitRepository.findById(unitId)
                .orElseThrow(() -> new RuntimeException("UNIT_NOT_FOUND"));

        equipmentUnitRepository.delete(unit);
    }
}
