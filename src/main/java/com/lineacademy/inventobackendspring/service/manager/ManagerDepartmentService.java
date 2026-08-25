package com.lineacademy.inventobackendspring.service.manager;

import com.lineacademy.inventobackendspring.domain.department.Department;
import com.lineacademy.inventobackendspring.domain.enums.MemberRole;
import com.lineacademy.inventobackendspring.domain.enums.MemberStatus;
import com.lineacademy.inventobackendspring.domain.member.Member;
import com.lineacademy.inventobackendspring.dto.manager.department.request.TransferDepartmentRequest;
import com.lineacademy.inventobackendspring.repository.DepartmentRepository;
import com.lineacademy.inventobackendspring.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagerDepartmentService {

    private final MemberRepository memberRepository;
    private final DepartmentRepository departmentRepository;

    private Member getManagerMemberByUserId(Long userId) {
        Member member = memberRepository.findFirstByUserIdAndStatus(userId, MemberStatus.APPROVED)
                .orElseThrow(() -> new RuntimeException("FORBIDDEN_APPROVAL"));

        if (member.getRole() == MemberRole.MEMBER) {
            throw new RuntimeException("FORBIDDEN_APPROVAL");
        }
        return member;
    }

    @Transactional(readOnly = true)
    public List<Member> getOrgMemberList(Long userId, String search) {
        Member manager = getManagerMemberByUserId(userId);

        return memberRepository.findOrgMembersWithSearch(
                manager.getOrganization().getId(),
                MemberStatus.APPROVED,
                search
        );
    }

    @Transactional(readOnly = true)
    public Department getDepartmentById(Long userId, Long dpId) {
        Member manager = getManagerMemberByUserId(userId);

        Department department = departmentRepository.findById(dpId)
                .orElseThrow(() -> new RuntimeException("DEPARTMENT_NOT_FOUND"));

        if (!department.getOrganization().getId().equals(manager.getOrganization().getId())) {
            throw new RuntimeException("FORBIDDEN_DEPARTMENT_VIEW");
        }

        return department;
    }

    @Transactional
    public void transferDepartment(Long userId, TransferDepartmentRequest request) {
        Member manager = getManagerMemberByUserId(userId);

        // 부서가 존재하고, 관리자와 같은 조직인지 확인
        Department targetDepartment = departmentRepository.findById(request.getTargetDepartmentId())
                .orElseThrow(() -> new RuntimeException("DEPARTMENT_NOT_FOUND"));

        if (!targetDepartment.getOrganization().getId().equals(manager.getOrganization().getId())) {
            throw new RuntimeException("DEPARTMENT_NOT_FOUND");
        }

        // 선택된 멤버들의 부서 ID 일괄 변경 (@Modifying 쿼리 실행)
        memberRepository.updateDepartmentForMembers(
                targetDepartment,
                request.getMemberIds(),
                manager.getOrganization().getId()
        );
    }
}