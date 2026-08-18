package com.example.it_iap.entity.enums;

public enum OrderStatus {
    PENDING,    // Vừa tạo link, đang chờ khách quét mã
    PAID,       // Đã thanh toán thành công (Webhook báo về)
    EXPIRED,     // Quá hạn
    CANCELLED       //Hủy
}
