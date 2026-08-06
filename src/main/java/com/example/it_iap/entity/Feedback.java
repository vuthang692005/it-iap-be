package com.example.it_iap.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
// 1. Ghi đè lệnh DELETE mặc định thành UPDATE
@SQLDelete(sql = "UPDATE feedback SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
// 2. Tự động thêm điều kiện này vào MỌI câu lệnh SELECT
@SQLRestriction("deleted_at IS NULL")
public class Feedback extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String imageUrl;

    private int rating;

    @Column(columnDefinition = "TEXT")
    private String adminReply;

    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
