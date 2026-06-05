package com.example.it_iap.repository;

import com.example.it_iap.entity.PromptVersion;
import com.example.it_iap.entity.enums.PromptUseCase;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PromptVersionRepository extends JpaRepository<PromptVersion, Long> {
    boolean existsByAdminPromptIdAndVersion(Long id, String version);

    Optional<PromptVersion> findFirstByAdminPromptApplyForAndLastActivatedAtIsNotNullOrderByLastActivatedAtDesc(
            PromptUseCase useCase
    );

    @EntityGraph(attributePaths = {"adminPrompt"})
    Optional<PromptVersion> findByAdminPromptPromptKeyAndVersion(String promptKey, String version);

    @Query("SELECT CASE WHEN COUNT(pv) > 0 THEN true ELSE false END FROM PromptVersion pv " +
            "WHERE pv.id = :id " +
            "AND pv.lastActivatedAt IS NOT NULL " +
            "AND pv.lastActivatedAt = (" +
            "    SELECT MAX(pv2.lastActivatedAt) FROM PromptVersion pv2 " +
            "    JOIN pv2.adminPrompt ap2 " +
            "    WHERE ap2.applyFor = pv.adminPrompt.applyFor" +
            ")")
    boolean isActiveVersion(@Param("id") Long id);
}
