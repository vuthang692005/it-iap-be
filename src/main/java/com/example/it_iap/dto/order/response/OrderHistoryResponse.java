package com.example.it_iap.dto.order.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class OrderHistoryResponse {
    private Long orderCode;
    private String productName;
    private Integer quantity;
    private Long originalPrice;
    private Long discountAmount;  // Tiền voucher
    private Long upgradeDiscount; // Tiền dư gói cũ
    private Long amount;          // Tiền thực trả
    private String status;        // PENDING, PAID, CANCELLED
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
}
