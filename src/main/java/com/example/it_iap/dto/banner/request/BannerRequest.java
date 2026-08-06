package com.example.it_iap.dto.banner.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class BannerRequest {
    @NotBlank(message = "TITLE_INVALID")
    private String title;

    @NotBlank(message = "BANNER_CONTENT_INVALID")
    private String content;

    private MultipartFile image;

    private boolean isActive;

    private String marquee;
}
