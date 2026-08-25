package com.lineacademy.inventobackendspring.repository;

import com.lineacademy.inventobackendspring.domain.department.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findFirstByOrganizationIdAndNameContainingIgnoreCase(Long organizationId, String name);

    List<Department> findByOrganizationIdOrderByNameAsc(Long organizationId);

    // 조직 내 부서 목록을 생성일 오름차순으로 조회 (TS의 orderBy: { createdAt: "asc" })
    List<Department> findByOrganizationIdOrderByCreatedAtAsc(Long organizationId);

    // 조직 ID와 부서 ID로 특정 부서 조회 (검증용)
    Optional<Department> findByIdAndOrganizationId(Long id, Long organizationId);
}