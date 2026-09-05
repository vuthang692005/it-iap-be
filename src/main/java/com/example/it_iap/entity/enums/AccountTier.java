package com.example.it_iap.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Period;

@Getter
@RequiredArgsConstructor
public enum AccountTier {
    BASIC(0, 0L, null,
            "Gói Cơ Bản", "Tài khoản miễn phí",
            1, 2, 8000),

    PLUS_MONTH(1, 39000L, Period.ofMonths(1),
            "Gói Plus (Tháng)", "Nâng cấp tài khoản Plus",
            3, 6, 24000),

//    PLUS_YEAR(2, 500000L, Period.ofYears(1),
//            "Gói Plus (Năm)", "Nâng cấp tài khoản Plus",
//            6, 10, 32000),

    PRO_MONTH(3, 89000L, Period.ofMonths(1),
            "Gói Pro (Tháng)", "Nâng cấp tài khoản Pro",
            6, 15, 48000),

//    PRO_YEAR(4, 1000000L, Period.ofYears(1),
//            "Gói Pro (Năm)", "Nâng cấp tài khoản Pro",
//            12, 20, 48000)

    ;
    private final int level;
    private final Long price;
    private final Period duration;
    private final String productName;
    private final String description;
    private final int maxProfiles;
    private final int maxDailyInterviews;
    private final int maxChatbotTokens;

    public static AccountTier from(String value) {
        if (value == null) return null;

        try {
            return AccountTier.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
