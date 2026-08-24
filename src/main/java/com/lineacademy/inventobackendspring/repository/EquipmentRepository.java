package com.lineacademy.inventobackendspring.repository;

import com.lineacademy.inventobackendspring.domain.equipment.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
}
