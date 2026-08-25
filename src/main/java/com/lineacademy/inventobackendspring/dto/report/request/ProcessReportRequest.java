package com.lineacademy.inventobackendspring.dto.report.request;

import com.lineacademy.inventobackendspring.domain.enums.ReportType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcessReportRequest {
    @NotNull(message = "올바른 보고 유형을 선택해주세요.")
    private ReportType type;

    @NotBlank(message = "처리 결과를 입력해주세요.")
    private String result;
}
