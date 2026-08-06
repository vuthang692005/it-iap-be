package com.example.it_iap.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRank {
    DIAMOND("Hạng Kim Cương"),
    GOLD("Hạng Vàng"),
    SILVER("Hạng Bạc"),
    BRONZE("Hạng Đồng");

    private final String displayName;
}
