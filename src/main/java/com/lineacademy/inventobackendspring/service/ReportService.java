package com.lineacademy.inventobackendspring.service;

import com.lineacademy.inventobackendspring.domain.enums.MemberRole;
import com.lineacademy.inventobackendspring.domain.enums.ReportStatus;
import com.lineacademy.inventobackendspring.domain.enums.RentalStatus;
import com.lineacademy.inventobackendspring.domain.enums.MemberStatus;
import com.lineacademy.inventobackendspring.domain.enums.ReportType;
import com.lineacademy.inventobackendspring.domain.equipment.Equipment;
import com.lineacademy.inventobackendspring.domain.member.Member;
import com.lineacademy.inventobackendspring.domain.report.Report;
import com.lineacademy.inventobackendspring.dto.report.request.CreateReportRequest;
import com.lineacademy.inventobackendspring.dto.report.request.ProcessReportRequest;
import com.lineacademy.inventobackendspring.dto.report.request.UpdateReportRequest;
import com.lineacademy.inventobackendspring.repository.EquipmentRepository;
import com.lineacademy.inventobackendspring.repository.MemberRepository;
import com.lineacademy.inventobackendspring.repository.RentalRepository;
import com.lineacademy.inventobackendspring.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    private final EquipmentRepository equipmentRepository;
    private final RentalRepository rentalRepository;

    private Member getMemberByUserId(Long userId) {
        return memberRepository.findFirstByUserIdAndStatus(userId, MemberStatus.APPROVED)
                .orElseThrow(() -> new RuntimeException("MEMBER_NOT_FOUND"));
    }

    private Member getManagerMemberByUserId(Long userId, Long ozId) {
        return memberRepository.findFirstByUserIdAndOrganizationIdAndStatusAndRoleIn(
                        userId, ozId, MemberStatus.APPROVED, Arrays.asList(MemberRole.OWNER, MemberRole.MANAGER))
                .orElseThrow(() -> new RuntimeException("MANAGER_PERMISSION_REQUIRED"));
    }

    @Transactional
    public Report createReport(Long userId, CreateReportRequest request) {
        Member member = getMemberByUserId(userId);

        Equipment equipment = null;
        if (request.getEquipmentId() != null) {
            equipment = equipmentRepository.findById(request.getEquipmentId()).orElse(null);
        }

        if (request.getType() == ReportType.BROKEN && request.getEquipmentId() != null) {
            boolean isBorrowed = rentalRepository.existsByMemberIdAndEquipmentIdAndStatus(
                    member.getId(), request.getEquipmentId(), RentalStatus.BORROWED);
            if (!isBorrowed) {
                throw new RuntimeException("BORROWED_RENTAL_REQUIRED");
            }

            boolean isAlreadyPending = reportRepository.existsByReporterIdAndEquipmentIdAndTypeAndStatus(
                    member.getId(), request.getEquipmentId(), ReportType.BROKEN, ReportStatus.PENDING);
            if (isAlreadyPending) {
                throw new RuntimeException("PENDING_REPORT_ALREADY_EXISTS");
            }
        }

        Report report = Report.builder()
                .reporter(member)
                .type(request.getType())
                .title(request.getTitle())
                .content(request.getContent())
                .equipment(equipment)
                .status(ReportStatus.PENDING)
                .build();

        return reportRepository.save(report);
    }

    @Transactional(readOnly = true)
    public List<Report> getReportList(Long userId, Long ozId) {
        if (ozId != null) {
            getManagerMemberByUserId(userId, ozId);
            return reportRepository.findByReporterOrganizationIdOrderByCreatedAtDesc(ozId);
        } else {
            Member member = getMemberByUserId(userId);
            return reportRepository.findByReporterIdOrderByCreatedAtDesc(member.getId());
        }
    }

    @Transactional(readOnly = true)
    public Report getReportById(Long userId, Long reportId) {
        Member member = getMemberByUserId(userId);

        Report report = reportRepository.findByIdAndReporterOrganizationId(reportId, member.getOrganization().getId())
                .orElseThrow(() -> new RuntimeException("REPORT_NOT_FOUND"));

        if (member.getRole() == MemberRole.MEMBER && !report.getReporter().getId().equals(member.getId())) {
            throw new RuntimeException("REPORT_NOT_FOUND");
        }

        return report;
    }

    @Transactional
    public Report processReport(Long userId, Long reportId, ProcessReportRequest request) {
        Member manager = memberRepository.findFirstByUserIdAndStatusAndRoleIn(
                        userId, MemberStatus.APPROVED, Arrays.asList(MemberRole.OWNER, MemberRole.MANAGER))
                .orElseThrow(() -> new RuntimeException("MANAGER_PERMISSION_REQUIRED"));

        Report report = reportRepository.findByIdAndReporterOrganizationId(reportId, manager.getOrganization().getId())
                .orElseThrow(() -> new RuntimeException("REPORT_NOT_FOUND"));

        if (report.getStatus() == ReportStatus.COMPLETED) {
            throw new RuntimeException("REPORT_ALREADY_PROCESSED");
        }

        report.processReport(manager, ReportStatus.COMPLETED, request.getType(), request.getResult());

        return report;
    }

    @Transactional
    public Report updateReport(Long userId, Long reportId, UpdateReportRequest request) {
        Member member = getMemberByUserId(userId);

        Report report = reportRepository.findByIdAndReporterId(reportId, member.getId())
                .orElseThrow(() -> new RuntimeException("REPORT_NOT_FOUND"));

        if (report.getStatus() == ReportStatus.COMPLETED) {
            throw new RuntimeException("CANNOT_UPDATE_COMPLETED_REPORT");
        }

        if (request.getType() != null) report.updateType(request.getType());
        if (request.getTitle() != null) report.updateTitle(request.getTitle());
        if (request.getContent() != null) report.updateContent(request.getContent());

        return report;
    }

    @Transactional
    public void deleteReport(Long userId, Long reportId) {
        Member member = getMemberByUserId(userId);

        Report report = reportRepository.findByIdAndReporterId(reportId, member.getId())
                .orElseThrow(() -> new RuntimeException("REPORT_NOT_FOUND"));

        if (report.getStatus() == ReportStatus.COMPLETED) {
            throw new RuntimeException("CANNOT_CANCEL_COMPLETED_REPORT");
        }

        reportRepository.delete(report);
    }
}