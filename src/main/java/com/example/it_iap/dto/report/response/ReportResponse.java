package com.example.it_iap.dto.report.response;

import com.example.it_iap.entity.enums.ReportStatus;
import com.example.it_iap.entity.enums.ReportType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class ReportResponse {
    private long reportId;

    private long interviewQuestionId;

    private long interviewId;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime createdAt;

    private String userEmail;

    private String description;

    private ReportStatus status;

    private ReportType reportType;

    private String adminReply;
}
