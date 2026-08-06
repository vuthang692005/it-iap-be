package com.example.it_iap.dto.order.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderPreviewResponse {
    private Long originalPrice;      // Giá gốc (Chưa trừ gì)
    private Long remainingValue;     // Tiền dư từ gói cũ khi nâng cấp
    private Long promotionDiscount;  // Số tiền được giảm từ mã khuyến mãi
    private Long finalAmount;        // Số tiền thực tế khách phải trả
}
