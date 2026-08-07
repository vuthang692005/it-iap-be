package com.example.it_iap.dto.promotion.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TierInfoResponse {
    private String tierCode;       // PRO_MONTH, PLUS_YEAR...
    private String productName;    // Gói Pro (1 Tháng)
    private String description;    // Mô tả gói
    private Long originalPrice;    // Giá gốc
    private Integer level;         // Cấp độ (để sort giao diện)
    private PromotionResponse activePromotion; // Thông tin khuyến mãi (nếu có)
}
