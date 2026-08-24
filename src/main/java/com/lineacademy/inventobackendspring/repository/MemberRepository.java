package com.lineacademy.inventobackendspring.repository;

import com.lineacademy.inventobackendspring.domain.enums.MemberStatus;
import com.lineacademy.inventobackendspring.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserId(Long userId);

    boolean existsByUserIdAndOrganizationIdAndStatus(Long userId, Long organizationId, MemberStatus status);
}
