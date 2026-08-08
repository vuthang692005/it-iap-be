package com.example.it_iap.repository;

import com.example.it_iap.entity.Profile;
import com.example.it_iap.entity.enums.InterviewStatus;
import com.example.it_iap.entity.enums.TargetLevel;
import com.example.it_iap.entity.enums.TargetPosition;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    @EntityGraph(
            attributePaths = {"user"}
    )
    Optional<Profile> findWithUserByIdAndDeletedAtIsNull(Long id);

    List<Profile> findAllByUserIdAndDeletedAtIsNull(UUID userId);

    @Query("""
        SELECT p.targetPosition AS position, COUNT(p.id) AS count
        FROM Profile p
        WHERE (:level IS NULL OR p.targetLevel = :level)
          AND EXISTS (
              SELECT 1 FROM Interview i
              WHERE i.profile = p
                AND i.status IN :validStatuses
                AND i.createdAt BETWEEN :startDate AND :endDate
          )
        GROUP BY p.targetPosition
        ORDER BY count DESC
    """)
    List<PositionDistributionProjection> countProfilesByPosition(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("level") TargetLevel level,
            @Param("validStatuses") List<InterviewStatus> validStatuses
    );

    interface PositionDistributionProjection {
        TargetPosition getPosition();
        Long getCount();
    }

    int countByUserIdAndDeletedAtIsNull(UUID id);

    int countByUserIdAndIdLessThanEqualAndDeletedAtIsNull(UUID userId, Long profileId);
}
