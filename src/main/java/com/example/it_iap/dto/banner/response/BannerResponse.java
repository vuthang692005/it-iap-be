package com.example.it_iap.dto.banner.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BannerResponse {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private boolean isActive;
    private String marquee;
}
