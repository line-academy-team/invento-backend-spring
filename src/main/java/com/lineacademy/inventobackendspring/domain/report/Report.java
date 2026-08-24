package com.lineacademy.inventobackendspring.domain.report;

import com.lineacardemy.inventobackendspring.domain.common.BaseTimeEntity;
import com.lineacardemy.inventobackendspring.domain.enums.ReportStatus;
import com.lineacardemy.inventobackendspring.domain.enums.ReportType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.lang.reflect.Member;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status = ReportStatus.PENDING;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(columnDefinition = "TEXT")
    private String result;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by")
    private Member processor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private Member reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @Builder
    private Report (
            Equipment equipment
            Member reporter,
            ReportType type,
            ReportStatus status,
            String title,
            String content
    ) {
        this.equipment = equipment;
                this.reporter = reporter;
                this.type = type;
                if (status ! = null) this.status = status;
                this.title = title;
                this.content = content;
    }

    public void processReport(
            Member processor,
            ReportStatus status,
            String result
    ) {
        this.processor = processor;
        this.status = status;
        this.result = result;
        this.processedAt = LocalDateTime.now();
    }


}
