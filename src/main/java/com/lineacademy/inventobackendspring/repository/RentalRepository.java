package com.lineacademy.inventobackendspring.repository;

import com.lineacademy.inventobackendspring.domain.rental.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    List<Rental> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    Optional<Rental> findByIdAndMemberId(Long id, Long memberId);

    List<Rental> findByEquipmentOrganizationIdOrderByCreatedAtDesc(Long organizationId);

    Optional<Rental> findByIdAndEquipmentOrganizationId(Long id, Long organizationId);
}
