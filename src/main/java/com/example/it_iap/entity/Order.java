package com.example.it_iap.entity;

import com.example.it_iap.entity.enums.AccountTier;
import com.example.it_iap.entity.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long orderCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountTier accountTier;

    @Column(nullable = false)
    private Integer quantity = 1;

    // GIÁ GỐC CỦA GÓI (Lấy từ Enum tại thời điểm tạo đơn)
    @Column(nullable = false)
    private Long originalPrice;

    // SỐ TIỀN ĐƯỢC KHUYẾN MÃI (Nếu không có mã giảm giá thì mặc định là 0)
    @Column(nullable = false)
    private Long discountAmount = 0L;

    // SỐ TIỀN DƯ TỪ GÓI CŨ KHI NÂNG CẤP
    @Column(nullable = false)
    private Long upgradeDiscount = 0L; // 🌟 MỚI: Chỉ lưu tiền dư gói cũ

    // Số tiền THỰC TẾ yêu cầu khách thanh toán qua PayOS (Đã trừ Khuyến mãi)
    @Column(nullable = false)
    private Long amount;

    @Column(columnDefinition = "TEXT")
    private String checkoutUrl;

    @Column(columnDefinition = "TEXT")
    private String QRCode;

    private String bankReference; // Mã giao dịch

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    private LocalDateTime expiredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;
}
