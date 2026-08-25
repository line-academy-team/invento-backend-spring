package com.lineacademy.inventobackendspring.service.owner;

import com.lineacademy.inventobackendspring.domain.department.Department;
import com.lineacademy.inventobackendspring.domain.enums.MemberRole;
import com.lineacademy.inventobackendspring.domain.enums.MemberStatus;
import com.lineacademy.inventobackendspring.domain.member.Member;
import com.lineacademy.inventobackendspring.dto.owner.department.request.AssignDepartmentManagerRequest;
import com.lineacademy.inventobackendspring.dto.owner.department.request.DepartmentRequest;
import com.lineacademy.inventobackendspring.repository.DepartmentRepository;
import com.lineacademy.inventobackendspring.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OwnerDepartmentService {

    private final DepartmentRepository departmentRepository;
    private final MemberRepository memberRepository;

    private Member getApprovedMemberByUserId(Long userId) {
        return memberRepository.findFirstByUserIdAndStatus(userId, MemberStatus.APPROVED)
                .orElseThrow(() -> new RuntimeException("FORBIDDEN_APPROVAL"));
    }

    private Member getOwnerMemberByUserId(Long userId) {
        Member member = getApprovedMemberByUserId(userId);
        if (member.getRole() != MemberRole.OWNER) {
            throw new RuntimeException("FORBIDDEN_OWNER_ONLY");
        }
        return member;
    }

    @Transactional(readOnly = true)
    public List<Department> getDepartmentList(Long userId) {
        Member member = getApprovedMemberByUserId(userId);

        if (member.getRole() == MemberRole.MEMBER) {
            throw new RuntimeException("FORBIDDEN_APPROVAL");
        }

        return departmentRepository.findByOrganizationIdOrderByCreatedAtAsc(member.getOrganization().getId());
    }

    @Transactional
    public Department createDepartment(Long userId, DepartmentRequest request) {
        Member owner = getOwnerMemberByUserId(userId);

        Department department = Department.builder()
                .organization(owner.getOrganization())
                .name(request.getName())
                .build();

        return departmentRepository.save(department);
    }

    @Transactional
    public Department updateDepartment(Long userId, Long dpId, DepartmentRequest request) {
        Member owner = getOwnerMemberByUserId(userId);

        Department department = departmentRepository.findByIdAndOrganizationId(dpId, owner.getOrganization().getId())
                .orElseThrow(() -> new RuntimeException("DEPARTMENT_NOT_FOUND"));

        department.updateName(request.getName());

        return department;
    }

    @Transactional
    public void deleteDepartment(Long userId, Long dpId) {
        Member owner = getOwnerMemberByUserId(userId);

        Department department = departmentRepository.findByIdAndOrganizationId(dpId, owner.getOrganization().getId())
                .orElseThrow(() -> new RuntimeException("DEPARTMENT_NOT_FOUND"));

        // 1. 해당 부서 멤버들의 departmentId를 null로 초기화
        memberRepository.updateDepartmentIdToNull(dpId);

        // 2. 부서 삭제
        departmentRepository.delete(department);
    }

    @Transactional
    public void assignDepartmentManager(Long userId, Long dpId, AssignDepartmentManagerRequest request) {
        Member owner = getOwnerMemberByUserId(userId);

        // 부서 검증
        departmentRepository.findByIdAndOrganizationId(dpId, owner.getOrganization().getId())
                .orElseThrow(() -> new RuntimeException("DEPARTMENT_NOT_FOUND"));

        // 임명할 타겟 멤버 검증 (동일 조직 & 동일 부서에 있는지 확인)
        Member targetMember = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new RuntimeException("MEMBER_NOT_IN_DEPARTMENT"));

        if (!targetMember.getOrganization().getId().equals(owner.getOrganization().getId()) ||
                targetMember.getDepartment() == null ||
                !targetMember.getDepartment().getId().equals(dpId)) {
            throw new RuntimeException("MEMBER_NOT_IN_DEPARTMENT");
        }

        // 1. 기존 해당 부서의 MANAGER들을 일반 MEMBER로 강등
        List<Member> currentManagers = memberRepository.findByDepartmentIdAndRole(dpId, MemberRole.MANAGER);
        for (Member manager : currentManagers) {
            manager.updateRole(MemberRole.MEMBER); // Entity 편의 메서드 추가 필요
        }

        // 2. 타겟 멤버를 MANAGER로 승격
        targetMember.updateRole(MemberRole.MANAGER);
    }
}