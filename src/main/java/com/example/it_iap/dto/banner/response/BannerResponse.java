package com.example.it_iap.dto.banner.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BannerResponse {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private Boolean isActive;
    private String marquee;
}
