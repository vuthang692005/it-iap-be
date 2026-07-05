package com.example.it_iap.repository;

import com.example.it_iap.entity.Reports;
import com.example.it_iap.entity.enums.ReportStatus;
import com.example.it_iap.entity.enums.ReportType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReportsRepository extends JpaRepository<Reports, Long> {
    @EntityGraph(attributePaths = {"user", "interviewQuestion.interview"})
    @Query("""
        SELECT r FROM Reports r
        WHERE (:reportType IS NULL OR r.reportType = :reportType)
          AND (:status IS NULL OR r.status = :status)
          AND (:email IS NULL OR LOWER(r.user.email) LIKE LOWER(CONCAT('%', :email, '%')))
    """)
    Page<Reports> searchReports(
            @Param("reportType") ReportType reportType,
            @Param("status") ReportStatus status,
            @Param("email") String email,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"user", "interviewQuestion.interview"})
    Optional<Reports> findReportById(Long id);
}