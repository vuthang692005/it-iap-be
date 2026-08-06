package com.example.it_iap.dto.order.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderResponse {
    private Long orderCode;      // Để Frontend query trạng thái đơn hàng
    private String checkoutUrl;  // Link để redirect khách sang trang thanh toán
    private String qrCode;       // Mã QR (nếu Frontend muốn tự vẽ)
    private String accountTier;  // Tên gói cước
    private Long amount;         // Số tiền thực tế khách phải trả
}
