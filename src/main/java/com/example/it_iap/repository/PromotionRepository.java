package com.example.it_iap.repository;

import com.example.it_iap.entity.Promotion;
import com.example.it_iap.entity.enums.AccountTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    Optional<Promotion> findByCode(String code);

    boolean existsByCode(String code);

    // Kiểm tra xem có khuyến mãi nào đang ACTIVE mà thời gian bị TỰA/TRÙNG lên nhau không
    @Query("SELECT COUNT(p) > 0 FROM Promotion p WHERE p.applicableTier = :tier " +
            "AND p.isActive = true " +
            "AND p.startDate <= :endDate " +
            "AND p.endDate >= :startDate " +
            "AND (:excludeId IS NULL OR p.id <> :excludeId)")
    boolean hasOverlappingPromotionForTier(@Param("tier") AccountTier tier,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate,
                                           @Param("excludeId") Long excludeId);

    // Lấy khuyến mãi ĐANG CHẠY (Thỏa mãn: Active, thời gian hiện tại nằm giữa Start và End)
    @Query("SELECT p FROM Promotion p WHERE p.applicableTier = :tier " +
            "AND p.isActive = true AND p.startDate <= :now AND p.endDate >= :now")
    Optional<Promotion> findActivePromotionByTier(@Param("tier") AccountTier tier,
                                                  @Param("now") LocalDateTime now);
}