package com.lineacademy.inventobackendspring.repository;

import com.lineacademy.inventobackendspring.domain.equipmentunit.EquipmentUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipmentUnitRepository extends JpaRepository<EquipmentUnit, Long> {

    List<EquipmentUnit> findByEquipmentIdOrderByCreatedAtDesc(Long equipmentId);
}
