package com.lineacademy.inventobackendspring.service;

import com.lineacademy.inventobackendspring.domain.department.Department;
import com.lineacademy.inventobackendspring.domain.equipment.Equipment;
import com.lineacademy.inventobackendspring.domain.member.Member;
import com.lineacademy.inventobackendspring.domain.enums.MemberRole;
import com.lineacademy.inventobackendspring.dto.equipment.request.CreateEquipmentRequest;
import com.lineacademy.inventobackendspring.dto.equipment.request.UpdateEquipmentRequest;
import com.lineacademy.inventobackendspring.repository.DepartmentRepository;
import com.lineacademy.inventobackendspring.repository.EquipmentRepository;
import com.lineacademy.inventobackendspring.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final MemberRepository memberRepository;
    private final DepartmentRepository departmentRepository;

    private Member getMemberByUserId(Long userId) {
        return memberRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("MEMBER_NOT_FOUND"));
    }

    @Transactional(readOnly = true)
    public List<Equipment> getEquipmentList(Long userId, String category, String search) {
        Member member = getMemberByUserId(userId);

        Long targetDepartmentId = member.getRole() == MemberRole.OWNER ? null :
                (member.getDepartment() != null ? member.getDepartment().getId() : null);

        return equipmentRepository.findEquipmentsByFilters(
                member.getOrganization().getId(),
                targetDepartmentId,
                category,
                search
        );
    }

    @Transactional(readOnly = true)
    public Equipment getEquipmentById(Long userId, Long equipmentId) {
        Member member = getMemberByUserId(userId);

        Long targetDepartmentId = member.getRole() == MemberRole.OWNER ? null :
                (member.getDepartment() != null ? member.getDepartment().getId() : null);

        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("EQUIPMENT_NOT_FOUND"));

        boolean isNotInOrg = !equipment.getOrganization().getId().equals(member.getOrganization().getId());
        boolean isNotInDept = targetDepartmentId != null &&
                (equipment.getDepartment() == null || !equipment.getDepartment().getId().equals(targetDepartmentId));

        if (isNotInOrg || isNotInDept) {
            throw new RuntimeException("EQUIPMENT_NOT_IN_ORGANIZARION_OR_DEPARTMENT");
        }

        return equipment;
    }

    @Transactional
    public Equipment createEquipment(Long userId, CreateEquipmentRequest request) {
        Member member = getMemberByUserId(userId);

        if (member.getRole() == MemberRole.MEMBER) {
            throw new RuntimeException("CANNOT_CREATE_EQUIPMENT_MEMBER");
        }

        Department targetDepartment = null;
        if (member.getRole() == MemberRole.OWNER) {
            if (request.getDepartmentId() != null) {
                targetDepartment = departmentRepository.findById(request.getDepartmentId())
                        .orElseThrow(() -> new RuntimeException("DEPARTMENT_NOT_FOUND"));
            }
        } else {
            targetDepartment = member.getDepartment();
        }

        Equipment equipment = Equipment.builder()
                .name(request.getName())
                .type(request.getType())
                .availableQuantity(request.getTotalQuantity())
                .totalQuantity(request.getTotalQuantity())
                .organization(member.getOrganization())
                .creator(member)
                .department(targetDepartment)
                .category(request.getCategory())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .build();

        return equipmentRepository.save(equipment);
    }

    @Transactional
    public Equipment updateEquipment(Long userId, Long equipmentId, UpdateEquipmentRequest request) {
        Member member = getMemberByUserId(userId);

        if (member.getRole() == MemberRole.MEMBER) {
            throw new RuntimeException("CANNOT_UPDATE_EQUIPMENT_MEMBER");
        }

        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("EQUIPMENT_NOT_FOUND"));

        if (request.getName() != null) equipment.updateName(request.getName());
        if (request.getType() != null) equipment.updateType(request.getType());
        if (request.getTotalQuantity() != null) equipment.updateTotalQuantity(request.getTotalQuantity());
        if (request.getCategory() != null) equipment.updateCategory(request.getCategory());
        if (request.getDescription() != null) equipment.updateDescription(request.getDescription());
        if (request.getImageUrl() != null) equipment.updateImageUrl(request.getImageUrl());

        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("DEPARTMENT_NOT_FOUND"));
            equipment.updateDepartment(dept);
        }

        return equipment;
    }

    @Transactional
    public void deleteEquipment(Long userId, Long equipmentId) {
        Member member = getMemberByUserId(userId);

        if (member.getRole() == MemberRole.MEMBER) {
            throw new RuntimeException("CANNOT_DELETE_EQUIPMENT_MEMBER");
        }

        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("EQUIPMENT_NOT_FOUND"));

        equipmentRepository.delete(equipment);
    }
}
