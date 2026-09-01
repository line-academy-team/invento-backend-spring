package com.lineacademy.inventobackendspring.repository;

import com.lineacademy.inventobackendspring.domain.equipment.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    @Query("SELECT e FROM Equipment e WHERE e.organization.id = :orgId " +
            "AND (:deptId IS NULL OR e.department.id = :deptId) " +
            "AND (:category IS NULL OR e.category = :category) " +
            "AND (:search IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "ORDER BY e.createdAt DESC")
    List<Equipment> findEquipmentsByFilters(
            @Param("orgId") Long orgId,
            @Param("deptId") Long deptId,
            @Param("category") String category,
            @Param("search") String search
    );

    @Query("SELECT COALESCE(SUM(e.totalQuantity), 0) FROM Equipment e WHERE e.organization.id = :organizationId")
    Integer sumTotalQuantityByOrganizationId(@Param("organizationId") Long organizationId);
}
