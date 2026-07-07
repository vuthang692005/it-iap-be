package com.example.it_iap.service;

import com.example.it_iap.dto.report.request.CreateReportRequest;
import com.example.it_iap.dto.report.request.SearchReportRequest;
import com.example.it_iap.dto.report.request.UpdateReportRequest;
import com.example.it_iap.dto.report.request.UserSearchReportRequest;
import com.example.it_iap.dto.report.response.ReportResponse;
import org.springframework.data.domain.Page;

public interface ReportService {
    void createReport (CreateReportRequest request);
    Page<ReportResponse> userSearchReport (UserSearchReportRequest request);
    Page<ReportResponse> searchReport (SearchReportRequest request);
    ReportResponse updateReport (long reportId, UpdateReportRequest request);
}
