package com.lineacademy.inventobackendspring.service;

import com.lineacademy.inventobackendspring.domain.department.Department;
import com.lineacademy.inventobackendspring.domain.enums.MemberRole;
import com.lineacademy.inventobackendspring.domain.enums.MemberStatus;
import com.lineacademy.inventobackendspring.domain.member.Member;
import com.lineacademy.inventobackendspring.domain.organization.Organization;
import com.lineacademy.inventobackendspring.domain.user.User;
import com.lineacademy.inventobackendspring.dto.organization.request.CreateOrganizationRequest;
import com.lineacademy.inventobackendspring.dto.organization.request.JoinOrganizationRequest;
import com.lineacademy.inventobackendspring.dto.organization.request.UpdateOrganizationRequest;
import com.lineacademy.inventobackendspring.repository.DepartmentRepository;
import com.lineacademy.inventobackendspring.repository.MemberRepository;
import com.lineacademy.inventobackendspring.repository.OrganizationRepository;
import com.lineacademy.inventobackendspring.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final MemberRepository memberRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    private Organization validateOwnerAndOrg(Long ozId, Long userId) {
        Organization org = organizationRepository.findByIdAndDeletedAtIsNull(ozId)
                .orElseThrow(() -> new RuntimeException("ORGANIZATION_NOT_FOUND"));
        if (!org.getCreator().getId().equals(userId)) {
            throw new RuntimeException("NOT_ORGANIZATION_OWNER");
        }
        return org;
    }

    private String generateUniqueInviteCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (organizationRepository.existsByInviteCode(code));
        return code;
    }

    @Transactional(readOnly = true)
    public Organization getOrganizationById(Long ozId, Long userId) {
        Organization organization = organizationRepository.findByIdAndDeletedAtIsNull(ozId)
                .orElseThrow(() -> new RuntimeException("ORGANIZATION_NOT_FOUND"));

        boolean isMember = organization.getMembers().stream()
                .anyMatch(member -> member.getUser().getId().equals(userId));

        if (!isMember) {
            throw new RuntimeException("NOT_A_MEMBER_OF_ORGANIZATION");
        }

        return organization;
    }

    @Transactional
    public Organization createOrganization(Long userId, @Valid CreateOrganizationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        if (organizationRepository.existsByCreatorIdAndDeletedAtIsNull(userId)) {
            throw new RuntimeException("ALREADY_CREATED_ORGANIZATION");
        }

        Organization org = Organization.builder()
                .name(request.getName())
                .description(request.getDescription())
                .logoUrl(request.getLogoUrl())
                .inviteCode(generateUniqueInviteCode())
                .creator(user)
                .build();

        organizationRepository.save(org);

        Member ownerMember = Member.builder()
                .user(user)
                .organization(org)
                .role(MemberRole.OWNER)
                .status(MemberStatus.APPROVED)
                .joinedAt(LocalDateTime.now())
                .build();

        memberRepository.save(ownerMember);

        return org;
    }

    @Transactional
    public Organization updateOrganization(Long ozId, Long userId, @Valid UpdateOrganizationRequest request) {
        Organization org = validateOwnerAndOrg(ozId, userId);

        if (request.getName() != null) org.updateName(request.getName());
        if (request.getDescription() != null) org.updateDescription(request.getDescription());
        if (request.getLogoUrl() != null) org.updateDescription(request.getLogoUrl());

        return org;
    }

    @Transactional
    public void deleteOrganization(Long ozId, Long userId) {
        Organization org = validateOwnerAndOrg(ozId, userId);
        org.markAsDeleted();
    }

    @Transactional
    public Member joinOrganization(Long userId, JoinOrganizationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        if (memberRepository.existsByUserIdAndStatusIn(userId, Arrays.asList(MemberStatus.PENDING, MemberStatus.APPROVED))) {
            throw new RuntimeException("ALREADY_JOINED_ANY_ORGANIZATION");
        }

        Organization organization = organizationRepository.findByInviteCodeAndDeletedAtIsNull(request.getInviteCode())
                .orElseThrow(() -> new RuntimeException("ORGANIZATION_NOT_FOUND"));

        Department department = null;
        if (request.getDepartment() != null && !request.getDepartment().isBlank()) {
            department = departmentRepository.findFirstByOrganizationIdAndNameContainingIgnoreCase(
                    organization.getId(), request.getDepartment()).orElse(null);

            if (department == null) throw new RuntimeException("DEPARTMENT_NOT_FOUND");
        }

        Member member = Member.builder()
                .user(user)
                .organization(organization)
                .role(MemberRole.MEMBER)
                .status(MemberStatus.PENDING)
                .build();

        return memberRepository.save(member);
    }
}
