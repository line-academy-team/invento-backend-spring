package com.lineacademy.inventobackendspring.dto.report.request;

import com.lineacademy.inventobackendspring.domain.enums.ReportType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CreateReportRequest {
    private Long equipmentId;

    @NotNull(message = "올바른 고보 유형을 선택해주세요")
    private ReportType type;

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 100, message = "제목은 최대 100자까지 입력 가능합니다.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
}
