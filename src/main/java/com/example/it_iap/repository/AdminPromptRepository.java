package com.example.it_iap.repository;

import com.example.it_iap.dto.adminPrompt.response.AdminPromptSummaryResponse;
import com.example.it_iap.entity.AdminPrompt;
import com.example.it_iap.entity.enums.PromptUseCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdminPromptRepository extends JpaRepository<AdminPrompt, Long> {
    boolean existsByPromptKey(String promptKey);

    @Query("SELECT new com.example.it_iap.dto.adminPrompt.response.AdminPromptSummaryResponse(" +
            "ap.id, ap.promptKey, pv.version, pv.provider, pv.model, ap.applyFor, " +
            "CASE WHEN pv.lastActivatedAt = (SELECT MAX(pv2.lastActivatedAt) FROM PromptVersion pv2 WHERE pv2.adminPrompt.applyFor = ap.applyFor) THEN true ELSE false END" +
            ") " +
            "FROM AdminPrompt ap " +
            "LEFT JOIN ap.promptVersions pv " +
            "WHERE (:promptKey IS NULL OR LOWER(ap.promptKey) LIKE LOWER(CONCAT('%', :promptKey, '%'))) " +
            "AND (:applyFor IS NULL OR ap.applyFor = :applyFor) " +
            "AND (" +
            "     (:active IS NULL AND (pv.id IS NULL OR pv.lastActivatedAt = (SELECT MAX(pv3.lastActivatedAt) FROM PromptVersion pv3 WHERE pv3.adminPrompt.id = ap.id) OR pv.lastActivatedAt IS NOT NULL)) " +
            "     OR (:active = true AND pv.lastActivatedAt = (SELECT MAX(pv4.lastActivatedAt) FROM PromptVersion pv4 WHERE pv4.adminPrompt.applyFor = ap.applyFor)) " +
            "     OR (:active = false AND (pv.id IS NULL OR pv.lastActivatedAt IS NULL OR pv.lastActivatedAt <> (SELECT MAX(pv5.lastActivatedAt) FROM PromptVersion pv5 WHERE pv5.adminPrompt.applyFor = ap.applyFor)))" +
            ")")
    Page<AdminPromptSummaryResponse> findAllSummaryWithFilters(
            @Param("promptKey") String promptKey,
            @Param("applyFor") PromptUseCase applyFor,
            @Param("active") Boolean active,
            Pageable pageable);
}
