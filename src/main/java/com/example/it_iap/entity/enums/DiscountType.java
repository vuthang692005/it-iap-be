package com.example.it_iap.entity.enums;

public enum DiscountType {
    PERCENTAGE,         // Giảm theo phần trăm (%)
    FIXED_AMOUNT;       // Giảm thẳng tiền mặt (VNĐ)

    public static DiscountType from(String value) {
        if (value == null) return null;

        try {
            return DiscountType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
