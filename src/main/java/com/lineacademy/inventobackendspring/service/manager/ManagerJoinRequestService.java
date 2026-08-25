package com.lineacademy.inventobackendspring.service.manager;

import com.lineacademy.inventobackendspring.domain.department.Department;
import com.lineacademy.inventobackendspring.domain.enums.MemberRole;
import com.lineacademy.inventobackendspring.domain.enums.MemberStatus;
import com.lineacademy.inventobackendspring.domain.member.Member;
import com.lineacademy.inventobackendspring.dto.manager.member.request.ProcessJoinRequest;
import com.lineacademy.inventobackendspring.repository.DepartmentRepository;
import com.lineacademy.inventobackendspring.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagerJoinRequestService {

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
    public List<Member> getJoinRequestList(Long userId, String search) {
        Member manager = getManagerMemberByUserId(userId);
        return memberRepository.findOrgJoinRequestsWithSearch(manager.getOrganization().getId(), search);
    }

    @Transactional(readOnly = true)
    public Object[] getJoinRequestById(Long userId, Long requesterId) {
        Member manager = getManagerMemberByUserId(userId);

        Member joinRequest = memberRepository.findByIdAndOrganizationId(requesterId, manager.getOrganization().getId())
                .orElseThrow(() -> new RuntimeException("JOIN_REQUEST_NOT_FOUND"));

        List<Department> departments = departmentRepository.findByOrganizationIdOrderByNameAsc(manager.getOrganization().getId());

        return new Object[]{joinRequest, departments};
    }

    @Transactional
    public void processJoinOrganization(Long userId, ProcessJoinRequest request) {
        Member manager = getManagerMemberByUserId(userId);

        Department targetDept = null;
        if (request.getStatus() == MemberStatus.APPROVED && request.getDepartmentId() != null) {
            targetDept = departmentRepository.findById(request.getDepartmentId()).orElse(null);
        }

        List<Member> targets = memberRepository.findAllById(request.getMemberIds());

        for (Member target : targets) {
            if (target.getOrganization().getId().equals(manager.getOrganization().getId())
                    && target.getStatus() == MemberStatus.PENDING) {

                if (request.getStatus() == MemberStatus.APPROVED) {
                    target.approveJoin(manager, targetDept);
                } else if (request.getStatus() == MemberStatus.REJECTED) {
                    target.rejectJoin(manager, request.getRejectedReason());
                }
            }
        }
    }
}