package com.example.it_iap.dto.report.request;

import com.example.it_iap.entity.enums.ReportStatus;
import com.example.it_iap.validator.annotation.EnumValue;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateReportRequest {
    @NotBlank(message = "REPORT_TYPE_INVALID")
    @EnumValue(enumClass = ReportStatus.class, message = "REPORT_TYPE_INVALID")
    private String status;

    @NotBlank(message = "ADMIN_REPLY_INVALID")
    private String adminReply;
}
