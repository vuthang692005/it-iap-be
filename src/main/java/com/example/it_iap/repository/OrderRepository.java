package com.example.it_iap.repository;

import com.example.it_iap.entity.Order;
import com.example.it_iap.entity.enums.OrderStatus;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderCode(@NonNull Long orderCode);

    List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId);

    // Tính tổng doanh thu trong 1 khoảng thời gian
    @Query("SELECT SUM(o.amount) FROM Order o WHERE o.status = :status AND o.createdAt BETWEEN :startDate AND :endDate")
    Long sumRevenueByStatusAndDateBetween(
            @Param("status") OrderStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Biểu đồ doanh thu theo Ngày
    @Query("""
        SELECT FUNCTION('DATE', o.createdAt) AS date, SUM(o.amount) AS total
        FROM Order o
        WHERE o.status = :status
          AND o.createdAt BETWEEN :startDate AND :endDate
        GROUP BY FUNCTION('DATE', o.createdAt)
        ORDER BY FUNCTION('DATE', o.createdAt) ASC
    """)
    List<RevenueTrendProjection> sumRevenueTrendsByDate(
            @Param("status") OrderStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    interface RevenueTrendProjection {
        java.sql.Date getDate();
        Long getTotal();
    }

    // Biểu đồ doanh thu theo Giờ
    @Query("""
        SELECT FUNCTION('DATE', o.createdAt) AS date,
               FUNCTION('HOUR', o.createdAt) AS hour,
               SUM(o.amount) AS total
        FROM Order o
        WHERE o.status = :status
          AND o.createdAt BETWEEN :startDate AND :endDate
        GROUP BY FUNCTION('DATE', o.createdAt), FUNCTION('HOUR', o.createdAt)
        ORDER BY FUNCTION('DATE', o.createdAt) ASC, FUNCTION('HOUR', o.createdAt) ASC
    """)
    List<HourlyRevenueTrendProjection> sumRevenueTrendsByHour(
            @Param("status") OrderStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    interface HourlyRevenueTrendProjection {
        java.sql.Date getDate();
        Integer getHour();
        Long getTotal();
    }
}