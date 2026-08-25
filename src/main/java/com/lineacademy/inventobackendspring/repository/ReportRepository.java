package com.lineacademy.inventobackendspring.repository;

import com.lineacademy.inventobackendspring.domain.enums.ReportStatus;
import com.lineacademy.inventobackendspring.domain.enums.ReportType;
import com.lineacademy.inventobackendspring.domain.report.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsByReporterIdAndEquipmentIdAndTypeAndStatus(Long reporterId, Long equipmentId, ReportType type, ReportStatus status);

    List<Report> findByReporterIdOrderByCreatedAtDesc(Long reporterId);

    List<Report> findByReporterOrganizationIdOrderByCreatedAtDesc(Long organizationId);

    Optional<Report> findByIdAndReporterId(Long id, Long reporterId);

    Optional<Report> findByIdAndReporterOrganizationId(Long id, Long organizationId);
}
