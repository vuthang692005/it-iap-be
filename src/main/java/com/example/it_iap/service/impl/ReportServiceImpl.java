package com.example.it_iap.service.impl;

import com.example.it_iap.dto.report.request.CreateReportRequest;
import com.example.it_iap.dto.report.request.SearchReportRequest;
import com.example.it_iap.dto.report.request.UpdateReportRequest;
import com.example.it_iap.dto.report.request.UserSearchReportRequest;
import com.example.it_iap.dto.report.response.ReportResponse;
import com.example.it_iap.entity.InterviewQuestion;
import com.example.it_iap.entity.Reports;
import com.example.it_iap.entity.User;
import com.example.it_iap.entity.enums.InterviewStatus;
import com.example.it_iap.entity.enums.ReportStatus;
import com.example.it_iap.entity.enums.ReportType;
import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import com.example.it_iap.repository.InterviewQuestionRepository;
import com.example.it_iap.repository.ReportsRepository;
import com.example.it_iap.service.ReportService;
import com.example.it_iap.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final ReportsRepository reportsRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;
    private final UserService userService;

    @Transactional
    public void createReport (CreateReportRequest request){
        User user = userService.getCurrentUser();
        ReportType reportType = ReportType.fromString(request.getReportType());

        InterviewQuestion interviewQuestion = interviewQuestionRepository
                .findValidQuestionForUser(request.getInterviewQuestionId(), user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_INTERVIEW_NOT_FOUND));

        if (interviewQuestion.getInterview().getStatus() != InterviewStatus.COMPLETED){
            throw new AppException(ErrorCode.INTERVIEW_NOT_COMPLETED);
        }

        Reports reports = new Reports();
        reports.setUser(user);
        reports.setReportType(reportType);
        reports.setInterviewQuestion(interviewQuestion);
        reports.setDescription(request.getDescription());

        reportsRepository.save(reports);
    }

    public Page<ReportResponse> userSearchReport (UserSearchReportRequest request) {
        User user = userService.getCurrentUser();
        SearchReportRequest searchReportRequest = new SearchReportRequest();
        searchReportRequest.setReportType(request.getReportType());
        searchReportRequest.setStatus(request.getStatus());
        searchReportRequest.setEmail(user.getEmail());
        searchReportRequest.setPages(request.getPages());

        return searchReport(searchReportRequest);
    }

    public Page<ReportResponse> searchReport (SearchReportRequest request) {
        int page = Math.max(0, request.getPages() - 1);
        int size = 10;
        PageRequest pageable = PageRequest.of(page, size, Sort.by("id").descending());
        ReportType reportType = ReportType.fromString(request.getReportType());
        ReportStatus reportStatus = ReportStatus.fromString(request.getStatus());

        Page<Reports> reports = reportsRepository.searchReports(reportType, reportStatus, request.getEmail(), pageable);
        return reports.map(this::buildReportResponse);
    }

    @Transactional
    public ReportResponse updateReport (long reportId, UpdateReportRequest request){
        ReportStatus reportStatus = ReportStatus.fromString(request.getStatus());
        Reports reports = reportsRepository.findReportById(reportId)
                .orElseThrow(() -> new AppException(ErrorCode.REPORT_NOT_FOUND));

        reports.setStatus(reportStatus);
        reports.setAdminReply(request.getAdminReply());

        reports = reportsRepository.save(reports);

        return buildReportResponse(reports);
    }

    private ReportResponse buildReportResponse (Reports reports){
        return new ReportResponse(
                reports.getId(),
                reports.getInterviewQuestion().getId(),
                reports.getInterviewQuestion().getInterview().getId(),
                reports.getCreatedAt(),
                reports.getUser().getEmail(),
                reports.getDescription(),
                reports.getStatus(),
                reports.getReportType(),
                reports.getAdminReply()
        );
    }
}
