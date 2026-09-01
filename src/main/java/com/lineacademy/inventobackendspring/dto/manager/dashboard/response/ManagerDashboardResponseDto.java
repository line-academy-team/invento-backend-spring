package com.lineacademy.inventobackendspring.dto.manager.dashboard.response;

import com.lineacademy.inventobackendspring.domain.rental.Rental;
import com.lineacademy.inventobackendspring.domain.enums.RentalStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ManagerDashboardResponseDto {

    @Getter
    @Builder
    public static class DashboardResponse {
        private SummaryDto summary;
        private List<RecentRentalDto> recentRentals;
    }

    @Getter
    @Builder
    public static class SummaryDto {
        private Integer totalEquipment;
        private Long borrowed;
        private Long requested;
        private Long brokenReports;
    }

    @Getter
    @Builder
    public static class RecentRentalDto {
        private Long id;
        private String equipment;
        private String name;
        private String date;
        private RentalStatus status;

        public static RecentRentalDto from(Rental rental) {
            // TS 코드의 date: `${month}.${day}` 를 Java DateTimeFormatter로 동일하게 구현
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM.dd");
            String formattedDate = rental.getRequestedAt() != null ? rental.getRequestedAt().format(formatter) : null;

            return RecentRentalDto.builder()
                    .id(rental.getId())
                    .equipment(rental.getEquipment().getName())
                    .name(rental.getMember().getUser().getName())
                    .date(formattedDate)
                    .status(rental.getStatus())
                    .build();
        }
    }
}