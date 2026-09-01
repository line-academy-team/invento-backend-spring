package com.lineacademy.inventobackendspring.service.manager;

import com.lineacademy.inventobackendspring.domain.member.Member;
import com.lineacademy.inventobackendspring.domain.enums.MemberRole;
import com.lineacademy.inventobackendspring.domain.enums.MemberStatus;
import com.lineacademy.inventobackendspring.domain.enums.RentalStatus;
import com.lineacademy.inventobackendspring.domain.enums.ReportStatus;
import com.lineacademy.inventobackendspring.domain.enums.ReportType;
import com.lineacademy.inventobackendspring.dto.manager.dashboard.response.ManagerDashboardResponseDto.*;
import com.lineacademy.inventobackendspring.repository.EquipmentRepository;
import com.lineacademy.inventobackendspring.repository.MemberRepository;
import com.lineacademy.inventobackendspring.repository.RentalRepository;
import com.lineacademy.inventobackendspring.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagerDashboardService {

    private final MemberRepository memberRepository;
    private final EquipmentRepository equipmentRepository;
    private final RentalRepository rentalRepository;
    private final ReportRepository reportRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboardData(Long userId) {
        Member manager = memberRepository.findFirstByUserIdAndOrganizationIdAndStatusAndRoleIn(
                userId, null, MemberStatus.APPROVED, Arrays.asList(MemberRole.OWNER, MemberRole.MANAGER)
        ).orElseThrow(() -> new RuntimeException("MANAGER_PERMISSION_REQUIRED"));

        Long orgId = manager.getOrganization().getId();

        // 1. 전체 장비 수량 합계
        Integer totalEquipment = equipmentRepository.sumTotalQuantityByOrganizationId(orgId);

        // 2. 대여 중인 장비 건수
        Long borrowedCount = rentalRepository.countByMemberOrganizationIdAndStatus(orgId, RentalStatus.BORROWED);

        // 3. 대여 요청 대기 건수
        Long requestCount = rentalRepository.countByMemberOrganizationIdAndStatus(orgId, RentalStatus.REQUESTED);

        // 4. 파손 신고 대기 건수
        Long brokenReportCount = reportRepository.countByReporterOrganizationIdAndTypeAndStatus(
                orgId, ReportType.BROKEN, ReportStatus.PENDING);

        // 5. 최근 대여 내역 (최대 10개)
        List<RecentRentalDto> recentRentals = rentalRepository.findTop10ByMemberOrganizationIdOrderByRequestedAtDesc(orgId)
                .stream()
                .map(RecentRentalDto::from)
                .collect(Collectors.toList());

        SummaryDto summary = SummaryDto.builder()
                .totalEquipment(totalEquipment)
                .borrowed(borrowedCount)
                .requested(requestCount)
                .brokenReports(brokenReportCount)
                .build();

        return DashboardResponse.builder()
                .summary(summary)
                .recentRentals(recentRentals)
                .build();
    }
}