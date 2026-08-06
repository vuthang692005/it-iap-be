package com.example.it_iap.repository;

import com.example.it_iap.entity.Order;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderCode(@NonNull Long orderCode);

    List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId);
}