package com.lineacademy.inventobackendspring.dto.report.request;

import com.lineacademy.inventobackendspring.domain.enums.ReportType;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateReportRequest {
    private ReportType type;

    @Size(max = 100, message = "제목은 최대 100자까지 입력 가능합니다.")
    private String title;

    private String content;
}
