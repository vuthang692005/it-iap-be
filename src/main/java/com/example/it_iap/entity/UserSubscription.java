package com.example.it_iap.entity;

import com.example.it_iap.entity.enums.AccountTier;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class UserSubscription extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountTier accountTier;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private Long planValue = 0L;

    @Column(nullable = false)
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Transient
    public AccountTier getActiveTier() {
        // Nếu endDate khác null (có hạn sử dụng) VÀ đã qua hạn -> Trả về BASIC
        if (this.endDate != null && this.endDate.isBefore(LocalDateTime.now())) {
            return AccountTier.BASIC;
        }
        // Còn hạn hoặc endDate = null (vĩnh viễn) -> Trả về gói trong DB
        return this.accountTier;
    }
}
