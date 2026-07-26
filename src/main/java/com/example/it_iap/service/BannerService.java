package com.example.it_iap.service;

import com.example.it_iap.dto.banner.request.BannerRequest;
import com.example.it_iap.dto.banner.response.BannerResponse;
import org.springframework.data.domain.Page;

public interface BannerService {
    Page<BannerResponse> getAllBanners(int page);
    BannerResponse getActiveBanner();
    BannerResponse createBanner(BannerRequest request);
    BannerResponse updateBannerInfo(Long id, BannerRequest request);
    void changeActiveStatus(Long id, boolean isActive);
}
