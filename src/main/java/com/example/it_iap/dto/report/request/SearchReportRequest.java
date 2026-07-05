package com.example.it_iap.dto.report.request;

import com.example.it_iap.entity.enums.ReportStatus;
import com.example.it_iap.entity.enums.ReportType;
import com.example.it_iap.validator.annotation.EnumValue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchReportRequest {
    @EnumValue(enumClass = ReportType.class, message = "REPORT_TYPE_INVALID")
    private String reportType;

    @EnumValue(enumClass = ReportStatus.class, message = "REPORT_STATUS_INVALID")
    private String status;

    private String email;

    public int pages;
}
