package com.lineacademy.inventobackendspring.repository;

import com.lineacademy.inventobackendspring.domain.organization.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findByIdAndDeletedAtIsNull(Long id);

    Optional<Organization> findByInviteCodeAndDeletedAtIsNull(String inviteCode);

    boolean existsByCreatorIdAndDeletedAtIsNull(Long creatorId);

    boolean existsByInviteCode(String inviteCode);
}
