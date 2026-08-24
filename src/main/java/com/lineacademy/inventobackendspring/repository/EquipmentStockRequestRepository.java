package com.lineacademy.inventobackendspring.repository;

import com.lineacademy.inventobackendspring.domain.equipmentstockrequest.EquipmentStockRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EquipmentStockRequestRepository extends
        JpaRepository<EquipmentStockRequest, Long> {

    List<EquipmentStockRequest> findAllByRequesterIdOrderByCreatedAtDesc(Long requesterId);

    List<EquipmentStockRequest> findAllByRequesterOrganizationIdOrderByCreatedAtDesc(Long organizationId);

    Optional<EquipmentStockRequest> findByIdAndRequesterId(Long id, Long requesterId);

}
