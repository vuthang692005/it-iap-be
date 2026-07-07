package com.example.it_iap.dto.report.request;

import com.example.it_iap.entity.enums.ReportType;
import com.example.it_iap.validator.annotation.EnumValue;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateReportRequest {
    private long interviewQuestionId;

    @NotBlank(message = "DESCRIPTION_INVALID")
    private String description;

    @NotBlank(message = "REPORT_TYPE_INVALID")
    @EnumValue(enumClass = ReportType.class, message = "REPORT_TYPE_INVALID")
    private String reportType;
}
