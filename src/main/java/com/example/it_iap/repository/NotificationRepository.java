package com.example.it_iap.repository;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.it_iap.dto.notification.response.NotificationResponse;
import com.example.it_iap.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("""
                SELECT new com.example.it_iap.dto.notification.response.NotificationResponse(
                    n.id,
                    n.title,
                    n.content,
                    n.type,
                    n.read,
                    n.link,
                    n.createdAt
                )
                FROM Notification n
                WHERE n.user.id = :id
                ORDER BY n.createdAt DESC
            """)
    Slice<NotificationResponse> findAllByUser_id(UUID id, Pageable pageable);

    int countByUser_idAndReadIsFalse(UUID userId);
}
